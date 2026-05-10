package com.assettrack.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.AllocationHistory;
import com.assettrack.domain.Asset;
import com.assettrack.domain.Status;
import com.assettrack.domain.User;
import com.assettrack.dto.AssetDetailDTO;
import com.assettrack.dto.AssetListItemDTO;
import com.assettrack.dto.AssetRequestDTO;
import com.assettrack.dto.AssetUpdateRequest;
import com.assettrack.dto.SpareLaptopDTO;
import com.assettrack.exception.ConflictException;
import com.assettrack.exception.ResourceNotFoundException;
import com.assettrack.mapper.AssetMapper;
import com.assettrack.repository.AssetRepository;
import com.assettrack.repository.AssetSpecification;
import com.assettrack.repository.HistoryRepository;

/**
 * Business logic layer for Core Asset Operations.
 * *
 * <p>
 * Handles asset creation, reading (fetching/searching), updating, and deleting.
 * Note: Allocation and return logic is handled by a separate module.
 */
@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final HistoryRepository historyRepository;

    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper, HistoryRepository historyRepository) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
        this.historyRepository = historyRepository;
    }

    /**
     * Creates a new asset from the provided request.
     *
     * @param request The data payload containing asset details.
     * @return The newly persisted Asset.
     * @throws ConflictException if the serial number is already registered.
     */
    public Asset createAsset(AssetRequestDTO request) {
        if (assetRepository.findBySerialNumber(request.getSerialNumber()).isPresent()) {
            throw new ConflictException("Serial number '" + request.getSerialNumber() + "' is already registered.");
        }

        Asset asset = assetMapper.toEntity(request);

        Status initialStatus = (request.getStatus() != null && !request.getStatus().isBlank())
                ? Status.valueOf(request.getStatus().toUpperCase())
                : Status.AVAILABLE;
        asset.setStatus(initialStatus);

        return assetRepository.save(asset);
    }

    /**
     * Retrieves a paginated list of assets mapped to AssetListItemDTO.
     * Prevents raw entities from leaking to the controller.
     *
     * @param pageable The pagination and sorting context.
     * @param search   Global text search (brand, model, serial number).
     * @param status   Filter by exact Status.
     * @param type     Filter by asset type.
     * @param brand    Filter by brand.
     * @return A Page of matching AssetListItemDTO.
     */
    @Transactional(readOnly = true)
    public Page<AssetListItemDTO> getAssets(Pageable pageable, String search, String status, String type, String brand) {
        return assetRepository.findAll(AssetSpecification.filterAssets(search, status, type, brand), pageable)
                .map(assetMapper::toListItemDto);
    }

    /**
     * Internal method to retrieve the raw Asset entity for updates/deletes.
     */
    @Transactional(readOnly = true)
    public Asset getAssetEntityById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
    }

    /**
     * Retrieves a single asset by its ID, mapped to AssetDetailDTO.
     *
     * @param id The ID of the asset.
     * @return The AssetDetailDTO.
     * @throws ResourceNotFoundException if the asset does not exist.
     */
    @Transactional(readOnly = true)
    public AssetDetailDTO getAssetById(Long id) {
        Asset asset = getAssetEntityById(id);
        return assetMapper.toDetailDto(asset);
    }

    /**
     * Updates an asset's details.
     *
     * @param id      The ID of the asset to update.
     * @param request The new data payload.
     * @return The updated Asset entity.
     */
    public Asset updateAsset(Long id, AssetUpdateRequest request) {
        Asset asset = getAssetEntityById(id);

        assetMapper.updateEntityFromDto(request, asset);

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            asset.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        }

        return assetRepository.save(asset);
    }

    /**
     * Permanently deletes an asset.
     *
     * @param id The ID of the asset to delete.
     * @throws ConflictException if the asset is currently assigned.
     */
    public void deleteAsset(Long id) {
        Asset asset = getAssetEntityById(id);

        if (Status.ASSIGNED.equals(asset.getStatus())) {
            throw new ConflictException("Cannot delete an asset that is currently assigned. Return it first.");
        }

        assetRepository.delete(asset);
    }

    /**
     * Finds the first available spare laptop.
     *
     * @return The spare laptop wrapped in a SpareLaptopDTO.
     * @throws ResourceNotFoundException if none is available.
     */
    @Transactional(readOnly = true)
    public SpareLaptopDTO findSpareLaptop() {
        Asset asset = assetRepository.findFirstSpareLaptop()
                .orElseThrow(() -> new ResourceNotFoundException("No spare laptop is currently available."));

        SpareLaptopDTO dto = new SpareLaptopDTO();
        dto.setId(asset.getId());
        dto.setBrand(asset.getBrand());
        dto.setModel(asset.getModel());
        dto.setStatus(asset.getStatus().name());

        // Find the last person who held this laptop to populate lastOwner.
        // We query the history repository directly and extract the User if a record exists.
        historyRepository.findFirstByAssetIdOrderByReturnedDateDesc(asset.getId())
                .ifPresent(history -> {
                    User user = history.getUser();
                    if (user != null) {
                        dto.setLastOwner(new SpareLaptopDTO.LastOwnerDTO(
                                user.getId(), 
                                user.getFirstName() + " " + user.getLastName()));
                    }
                });

        return dto;
    }

}