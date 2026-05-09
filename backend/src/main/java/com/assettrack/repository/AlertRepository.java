package com.assettrack.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.assettrack.domain.Alert;
import com.assettrack.domain.AlertType;

/**
 * Spring Data JPA repository for {@link Alert} entities.
 *
 * <p>Provides paginated lookups for active (unresolved) alerts,
 * with optional filtering by {@link AlertType}.
 */
public interface AlertRepository extends JpaRepository<Alert, Long> {

    /**
     * Returns all active (unresolved) alerts, newest first.
     * Used by the "Get Active Alerts" dashboard endpoint when no type filter is applied.
     *
     * @param pageable pagination/sorting parameters
     * @return a page of unresolved alerts
     */
    Page<Alert> findByResolvedFalse(Pageable pageable);

    /**
     * Returns active alerts of a specific type.
     * Used when the caller supplies a {@code type} query parameter.
     *
     * @param type     the alert category to filter by
     * @param pageable pagination/sorting parameters
     * @return a page of unresolved alerts matching the given type
     */
    Page<Alert> findByTypeAndResolvedFalse(AlertType type, Pageable pageable);

    /**
     * Checks whether an unresolved alert of the given type already exists
     * for a specific asset, generated after a given timestamp.
     * Used by the scheduled job to avoid creating duplicate alerts within
     * the same scheduler run.
     *
     * @param type      the alert type
     * @param assetId   the asset the alert relates to
     * @param createdAt the lower bound on creation time (exclusive)
     * @return {@code true} if a matching active alert already exists
     */
    boolean existsByTypeAndAssetIdAndResolvedFalseAndCreatedAtAfter(
            AlertType type, Long assetId, LocalDateTime createdAt);

    /**
     * Checks whether an unresolved low-stock alert whose message contains
     * a given substring already exists. Used to deduplicate type-level
     * LOW_STOCK alerts (which have no assetId).
     *
     * @param type            must be {@link AlertType#LOW_STOCK}
     * @param messageContains substring to match in the message (typically the type name)
     * @return {@code true} if a matching active alert already exists
     */
    boolean existsByTypeAndMessageContainingAndResolvedFalse(AlertType type, String messageContains);
}
