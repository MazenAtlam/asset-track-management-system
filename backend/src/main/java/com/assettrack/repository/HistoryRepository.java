package com.assettrack.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.assettrack.domain.AllocationHistory;

/**
 * Spring Data JPA repository for {@link AllocationHistory} entities.
 *
 * <p>Provides paginated chain-of-custody queries and a lookup for the
 * current open (un-returned) allocation record for a given asset.
 */
public interface HistoryRepository extends JpaRepository<AllocationHistory, Long> {

    /**
     * Returns all allocation records for a given asset, sorted by the
     * {@link Pageable} specification (typically by {@code assignedDate DESC}).
     * Used by the "Get Asset Allocation History" endpoint.
     *
     * @param assetId  the asset whose history is being requested
     * @param pageable pagination and sorting parameters
     * @return a page of history records for the specified asset
     */
    Page<AllocationHistory> findByAssetId(Long assetId, Pageable pageable);

    /**
     * Finds the single open allocation record for an asset — the row where
     * {@code returnedDate IS NULL}. There should be at most one such record
     * per asset at any point in time (enforced by service logic).
     * Used during the return/unallocate flow to close out the active record.
     *
     * @param assetId the asset whose active allocation is needed
     * @return an Optional containing the open history record, or empty if not currently allocated
     */
    Optional<AllocationHistory> findFirstByAssetIdAndReturnedDateIsNull(Long assetId);
}
