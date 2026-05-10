package com.assettrack.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.AllocationHistory;
import com.assettrack.domain.Asset;
import com.assettrack.domain.AssetCondition;
import com.assettrack.domain.Status;
import com.assettrack.domain.User;
import com.assettrack.dto.AllocationHistoryDTO;
import com.assettrack.dto.AllocationRequestDTO;
import com.assettrack.dto.ReturnRequestDTO;
import com.assettrack.exception.ConflictException;
import com.assettrack.exception.ResourceNotFoundException;
import com.assettrack.mapper.AllocationMapper;
import com.assettrack.repository.AssetRepository;
import com.assettrack.repository.HistoryRepository;
import com.assettrack.repository.UserRepository;

/**
 * Service for handling Asset Allocation and Return workflows.
 */
@Service
@Transactional
public class AllocationService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final AllocationMapper allocationMapper;

    public AllocationService(AssetRepository assetRepository,
                             UserRepository userRepository,
                             HistoryRepository historyRepository,
                             AllocationMapper allocationMapper) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.allocationMapper = allocationMapper;
    }

    /**
     * Allocates an asset to a user.
     * Ensures the asset is not already assigned or decommissioned before allocating.
     *
     * @param assetId The ID of the asset to allocate.
     * @param request The allocation request details.
     * @throws ConflictException if the asset is currently assigned or decommissioned.
     * @throws ResourceNotFoundException if the asset or user does not exist.
     */
    public void allocateAsset(Long assetId, AllocationRequestDTO request) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        if (Status.ASSIGNED.equals(asset.getStatus()) || Status.DECOMMISSIONED.equals(asset.getStatus())) {
            throw new ConflictException("Cannot allocate an asset that is currently " + asset.getStatus().name() + ".");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        asset.setStatus(Status.ASSIGNED);
        asset.setAssignedUser(user);

        AllocationHistory history = new AllocationHistory(asset, user, request.getAllocationDate());
        historyRepository.save(history);
    }

    /**
     * Returns a currently assigned asset.
     * Resolves the open history record and closes it out with the return date and condition.
     *
     * @param assetId The ID of the asset to return.
     * @param request The return request details.
     * @return A map containing success details according to the API spec.
     * @throws ConflictException if the asset is not currently assigned or if no active record is found.
     * @throws ResourceNotFoundException if the asset does not exist.
     */
    public Map<String, Object> returnAsset(Long assetId, ReturnRequestDTO request) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        if (!Status.ASSIGNED.equals(asset.getStatus())) {
            throw new ConflictException("Cannot return an asset that is not currently assigned.");
        }

        AllocationHistory history = historyRepository.findFirstByAssetIdAndReturnedDateIsNull(assetId)
                .orElseThrow(() -> new ConflictException("No active allocation record found for asset id: " + assetId));

        history.setReturnedDate(request.getReturnDate());
        history.setConditionUponReturn(AssetCondition.valueOf(request.getConditionUponReturn().toUpperCase()));

        Long previousOwnerId = history.getUser().getId();

        asset.setAssignedUser(null);
        Status newStatus = (request.getNewStatus() != null && !request.getNewStatus().isBlank())
                ? Status.valueOf(request.getNewStatus().toUpperCase())
                : Status.AVAILABLE;
        asset.setStatus(newStatus);

        historyRepository.save(history);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Asset returned successfully");
        response.put("assetId", assetId);
        response.put("previousOwnerId", previousOwnerId);
        response.put("currentStatus", newStatus.name());

        return response;
    }

    /**
     * Retrieves the paginated allocation history for an asset.
     *
     * @param assetId  The ID of the asset.
     * @param pageable The pagination parameters.
     * @return A paginated list of AllocationHistoryDTO.
     */
    @Transactional(readOnly = true)
    public Page<AllocationHistoryDTO> getAssetHistory(Long assetId, Pageable pageable) {
        return historyRepository.findByAssetId(assetId, pageable)
                .map(allocationMapper::toDto);
    }
}
