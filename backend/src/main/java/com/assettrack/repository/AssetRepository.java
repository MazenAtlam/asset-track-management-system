package com.assettrack.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assettrack.domain.Asset;
import com.assettrack.domain.Status;

/**
 * Spring Data JPA repository for {@link Asset} entities.
 *
 * <p>Extends {@link JpaSpecificationExecutor} to support the dynamic
 * filtering/sorting/search needed by the "Search & List Assets" endpoint.
 * Named queries are used for the less dynamic lookup patterns.
 */
public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    /**
     * Finds any asset whose serial number matches exactly, used during
     * registration to detect duplicate serial-number submissions.
     *
     * @param serialNumber the serial number to check
     * @return an Optional containing the matching asset, or empty if unique
     */
    Optional<Asset> findBySerialNumber(String serialNumber);

    /**
     * Returns the first available {@link Status#SPARE} laptop regardless of brand/model.
     * Used by the "Find Spare Laptop" quick action.
     *
     * <p>The query joins in the assigned user (currently null for spares) and
     * orders by id to return a deterministic result.
     *
     * @return an Optional containing the first spare laptop, or empty if none exists
     */
    @Query("SELECT a FROM Asset a WHERE LOWER(a.type) = 'laptop' AND a.status = 'SPARE' ORDER BY a.id ASC")
    Optional<Asset> findFirstSpareLaptop();

    /**
     * Counts how many assets of a given type currently have the given status.
     * Used by the scheduled low-stock check to determine whether an alert
     * should be raised.
     *
     * @param type   the asset type (case-insensitive match performed in service)
     * @param status the lifecycle status to filter by (typically AVAILABLE)
     * @return the count of matching assets
     */
    long countByTypeIgnoreCaseAndStatus(String type, Status status);

    /**
     * Returns all distinct asset types currently in the system.
     * Used by the low-stock job to iterate over every type without hard-coding them.
     *
     * @return list of distinct type strings (as stored, no case normalisation)
     */
    @Query("SELECT DISTINCT a.type FROM Asset a WHERE a.type IS NOT NULL")
    List<String> findDistinctTypes();

    /**
     * Finds assets whose warranty expiration date falls within the window
     * {@code [today, today + leadDays]}, used by the scheduled warranty-expiry check.
     *
     * @param today   the current date (inclusive lower bound)
     * @param cutoff  today plus the configured lead-time window (inclusive upper bound)
     * @return assets whose warranty expires within the window
     */
    @Query("SELECT a FROM Asset a WHERE a.warrantyExpirationDate BETWEEN :today AND :cutoff")
    List<Asset> findAssetsWithWarrantyExpiringSoon(@Param("today") LocalDate today,
                                                   @Param("cutoff") LocalDate cutoff);
}
