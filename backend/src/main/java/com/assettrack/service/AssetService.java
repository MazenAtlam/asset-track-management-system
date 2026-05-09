package com.assettrack.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.AllocationHistory;
import com.assettrack.domain.Asset;
import com.assettrack.domain.AssetCondition;
import com.assettrack.domain.Status;
import com.assettrack.domain.User;
import com.assettrack.dto.AllocationHistoryDTO;
import com.assettrack.dto.AllocationRequestDTO;
import com.assettrack.dto.AssetDetailDTO;
import com.assettrack.dto.AssetListItemDTO;
import com.assettrack.dto.AssetListItemDTO.AssignedUserDTO;
import com.assettrack.dto.AssetUpdateDTO;
import com.assettrack.dto.ReturnRequestDTO;
import com.assettrack.dto.SpareLaptopDTO;
import com.assettrack.dto.AssetDTO;
import com.assettrack.repository.AssetRepository;
import com.assettrack.repository.HistoryRepository;
import com.assettrack.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

/**
 * Business logic for §2.2 Asset Management.
 *
 * <p>Handles asset registration, searching/listing, detail retrieval,
 * updates, deletion, allocation, return, allocation history, and the
 * spare-laptop quick-action.
 *
 * <p>Role enforcement is done via {@code @PreAuthorize} annotations so that
 * the security contract is explicit and testable at the method level.
 */
@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    public AssetService(AssetRepository assetRepository,
                        HistoryRepository historyRepository,
                        UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // §2.2.1 — Register Asset  (POST /api/v1/assets)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Registers a new hardware asset.
     *
     * <p>Validates that the serial number is not already in use, then
     * persists the asset with {@link Status#AVAILABLE}.
     *
     * @param dto the asset registration request
     * @return the generated asset ID
     * @throws IllegalStateException if the serial number already exists
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Long registerAsset(AssetDTO dto) {
        if (assetRepository.findBySerialNumber(dto.getSerialNumber()).isPresent()) {
            throw new IllegalStateException(
                    "Serial number '" + dto.getSerialNumber() + "' is already registered.");
        }

        Asset asset = new Asset(
                dto.getType(),
                dto.getBrand(),
                dto.getModel(),
                dto.getSerialNumber(),
                dto.getPurchaseDate(),
                dto.getWarrantyExpirationDate(),
                Status.AVAILABLE
        );

        return assetRepository.save(asset).getId();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Search & List Assets  (GET /api/v1/assets)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated, filtered, sorted list of assets.
     *
     * <p>All parameters are optional — omitting them returns all assets
     * sorted by {@code id DESC} in pages of 10.
     *
     * @param page      1-based page number
     * @param size      page size
     * @param sortBy    entity field name to sort by
     * @param direction "asc" or "desc"
     * @param search    substring to match against type, brand, model, serialNumber
     * @param status    filter by exact status name
     * @param type      filter by asset type (case-insensitive)
     * @param brand     filter by brand (case-insensitive)
     * @return matching assets wrapped in a paged response map
     */
    @Transactional(readOnly = true)
    public Page<AssetListItemDTO> listAssets(int page, int size,
                                             String sortBy, String direction,
                                             String search, String status,
                                             String type, String brand) {
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Specification<Asset> spec = buildAssetSpec(search, status, type, brand);
        Page<Asset> assetPage = assetRepository.findAll(spec, pageable);

        return assetPage.map(this::toListItemDTO);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Get Asset Details  (GET /api/v1/assets/{id})
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns the full detail view for a specific asset.
     *
     * @param id the asset ID
     * @return the asset detail DTO
     * @throws EntityNotFoundException if no asset with that ID exists
     */
    @Transactional(readOnly = true)
    public AssetDetailDTO getAssetDetails(Long id) {
        Asset asset = findAssetById(id);
        return toDetailDTO(asset);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Update Asset Details / Status  (PUT /api/v1/assets/{id})
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Partially updates an asset's mutable fields.
     * Only non-null fields in {@code dto} are applied (PATCH semantics).
     *
     * @param id  the asset ID to update
     * @param dto the update payload
     * @return the updated status string
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateAsset(Long id, AssetUpdateDTO dto) {
        Asset asset = findAssetById(id);

        if (dto.getStatus() != null) {
            asset.setStatus(Status.valueOf(dto.getStatus().toUpperCase()));
        }
        if (dto.getType() != null)  asset.setType(dto.getType());
        if (dto.getBrand() != null) asset.setBrand(dto.getBrand());
        if (dto.getModel() != null) asset.setModel(dto.getModel());
        // notes field is informational only; stored in the response but not persisted
        // on the Asset entity itself (no separate notes column needed unless added later).

        assetRepository.save(asset);
        return asset.getStatus().name();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Delete Asset  (DELETE /api/v1/assets/{id})
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Permanently removes an asset from the system.
     *
     * <p>Deletion is rejected if the asset is currently ASSIGNED to prevent
     * orphaned allocation history records.
     *
     * @param id the asset ID to delete
     * @throws EntityNotFoundException if no asset with that ID exists
     * @throws IllegalStateException   if the asset is currently ASSIGNED
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAsset(Long id) {
        Asset asset = findAssetById(id);
        if (Status.ASSIGNED.equals(asset.getStatus())) {
            throw new IllegalStateException(
                    "Cannot delete an asset that is currently assigned. Return it first.");
        }
        assetRepository.delete(asset);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Find Spare Laptop  (GET /api/v1/assets/actions/spare-laptop)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns the first available spare laptop along with its last known owner.
     *
     * @return the spare laptop DTO
     * @throws EntityNotFoundException if no spare laptop is currently available
     */
    @Transactional(readOnly = true)
    public SpareLaptopDTO findSpareLaptop() {
        Asset asset = assetRepository.findFirstSpareLaptop()
                .orElseThrow(() -> new EntityNotFoundException("No spare laptop is currently available."));

        SpareLaptopDTO dto = new SpareLaptopDTO();
        dto.setId(asset.getId());
        dto.setBrand(asset.getBrand());
        dto.setModel(asset.getModel());
        dto.setStatus(asset.getStatus().name());

        // Find the most recent closed allocation (the last owner)
        historyRepository.findByAssetId(asset.getId(),
                        PageRequest.of(0, 1, Sort.by("assignedDate").descending()))
                .stream()
                .filter(h -> h.getReturnedDate() != null)   // only closed allocations
                .findFirst()
                .ifPresent(h -> {
                    User lastOwner = h.getUser();
                    dto.setLastOwner(new SpareLaptopDTO.LastOwnerDTO(
                            lastOwner.getId(),
                            lastOwner.getFirstName() + " " + lastOwner.getLastName()
                    ));
                });

        return dto;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // §2.2.2 — Allocate Asset  (POST /api/v1/assets/{id}/allocate)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Assigns an asset to a user.
     *
     * <p>Business rules:
     * <ul>
     *     <li>The asset must be AVAILABLE or SPARE (not ASSIGNED or DECOMMISSIONED).</li>
     *     <li>The target user must exist.</li>
     *     <li>A new {@link AllocationHistory} record is created to start the chain of custody.</li>
     *     <li>The asset status transitions to ASSIGNED.</li>
     * </ul>
     *
     * @param assetId the asset to allocate
     * @param dto     the allocation request
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void allocateAsset(Long assetId, AllocationRequestDTO dto) {
        Asset asset = findAssetById(assetId);

        if (Status.ASSIGNED.equals(asset.getStatus()) ||
                Status.DECOMMISSIONED.equals(asset.getStatus())) {
            throw new IllegalStateException("Asset is already assigned or decommissioned");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + dto.getUserId()));

        // Open a new allocation record
        AllocationHistory record = new AllocationHistory(asset, user, dto.getAllocationDate());
        historyRepository.save(record);

        // Update asset ownership
        asset.setStatus(Status.ASSIGNED);
        asset.setAssignedUser(user);
        assetRepository.save(asset);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Return/Unallocate Asset  (POST /api/v1/assets/{id}/return)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns an asset to the IT pool, closing the active allocation record.
     *
     * <p>Business rules:
     * <ul>
     *     <li>The asset must be currently ASSIGNED.</li>
     *     <li>The active history record's {@code returnedDate} and
     *         {@code conditionUponReturn} are set.</li>
     *     <li>The asset transitions to the caller-specified new status
     *         (AVAILABLE or SPARE), defaulting to AVAILABLE.</li>
     * </ul>
     *
     * @param assetId the asset to return
     * @param dto     the return request
     * @return the ID of the previous owner
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Long returnAsset(Long assetId, ReturnRequestDTO dto) {
        Asset asset = findAssetById(assetId);

        if (!Status.ASSIGNED.equals(asset.getStatus())) {
            throw new IllegalStateException("Asset is not currently assigned.");
        }

        // Close the open history record
        AllocationHistory record = historyRepository
                .findFirstByAssetIdAndReturnedDateIsNull(assetId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No open allocation record found for asset id: " + assetId));

        record.setReturnedDate(dto.getReturnDate());

        if (dto.getConditionUponReturn() != null) {
            record.setConditionUponReturn(
                    AssetCondition.valueOf(dto.getConditionUponReturn().toUpperCase()));
        }
        historyRepository.save(record);

        Long previousOwnerId = record.getUser().getId();

        // Determine new status (default to AVAILABLE)
        Status newStatus = Status.AVAILABLE;
        if (dto.getNewStatus() != null) {
            newStatus = Status.valueOf(dto.getNewStatus().toUpperCase());
        }

        asset.setStatus(newStatus);
        asset.setAssignedUser(null);
        assetRepository.save(asset);

        return previousOwnerId;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Get Asset Allocation History  (GET /api/v1/assets/{id}/history)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated chain of custody for the given asset.
     *
     * @param assetId the asset whose history is requested
     * @param page    1-based page number
     * @param size    page size
     * @return page of history entries
     */
    @Transactional(readOnly = true)
    public Page<AllocationHistoryDTO> getAssetHistory(Long assetId, int page, int size) {
        // Verify asset exists
        if (!assetRepository.existsById(assetId)) {
            throw new EntityNotFoundException("Asset not found with id: " + assetId);
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("assignedDate").descending());
        return historyRepository.findByAssetId(assetId, pageable)
                .map(this::toHistoryDTO);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private Asset findAssetById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with id: " + id));
    }

    private AssetListItemDTO toListItemDTO(Asset asset) {
        AssetListItemDTO dto = new AssetListItemDTO();
        dto.setId(asset.getId());
        dto.setType(asset.getType());
        dto.setBrand(asset.getBrand());
        dto.setModel(asset.getModel());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setStatus(asset.getStatus() != null ? asset.getStatus().name() : null);

        if (asset.getAssignedUser() != null) {
            User u = asset.getAssignedUser();
            dto.setAssignedUser(new AssignedUserDTO(
                    u.getId(),
                    u.getFirstName() + " " + u.getLastName()
            ));
        }
        return dto;
    }

    private AssetDetailDTO toDetailDTO(Asset asset) {
        AssetDetailDTO dto = new AssetDetailDTO();
        dto.setId(asset.getId());
        dto.setType(asset.getType());
        dto.setBrand(asset.getBrand());
        dto.setModel(asset.getModel());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setWarrantyExpirationDate(asset.getWarrantyExpirationDate());
        dto.setStatus(asset.getStatus() != null ? asset.getStatus().name() : null);

        if (asset.getAssignedUser() != null) {
            User u = asset.getAssignedUser();
            dto.setAssignedUser(new AssetDetailDTO.AssignedUserDTO(
                    u.getId(),
                    u.getFirstName() + " " + u.getLastName()
            ));
        }
        return dto;
    }

    private AllocationHistoryDTO toHistoryDTO(AllocationHistory h) {
        AllocationHistoryDTO dto = new AllocationHistoryDTO();
        dto.setHistoryId(h.getId());
        dto.setUserId(h.getUser().getId());
        dto.setUserName(h.getUser().getFirstName() + " " + h.getUser().getLastName());
        dto.setAssignedDate(h.getAssignedDate());
        dto.setReturnedDate(h.getReturnedDate());
        return dto;
    }

    /**
     * Builds a JPA Specification for the dynamic asset search/filter query.
     * All predicates are ANDed together; omitting a parameter means no
     * restriction on that dimension.
     */
    private Specification<Asset> buildAssetSpec(String search, String status,
                                                 String type, String brand) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("type")), like),
                        cb.like(cb.lower(root.get("brand")), like),
                        cb.like(cb.lower(root.get("model")), like),
                        cb.like(cb.lower(root.get("serialNumber")), like)
                ));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"),
                        Status.valueOf(status.toUpperCase())));
            }

            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("type")),
                        type.toLowerCase()));
            }

            if (brand != null && !brand.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")),
                        brand.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
