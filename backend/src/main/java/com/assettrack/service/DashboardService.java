package com.assettrack.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assettrack.domain.Alert;
import com.assettrack.domain.AlertType;
import com.assettrack.domain.Asset;
import com.assettrack.domain.Status;
import com.assettrack.dto.AlertDTO;
import com.assettrack.dto.DashboardSummaryDTO;
import com.assettrack.mapper.AlertMapper;
import com.assettrack.repository.AlertRepository;
import com.assettrack.repository.AssetRepository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

/**
 * §2.4 Reporting & Analytics — Dashboard summary and alerts listing.
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link #getSummary(LocalDate, LocalDate)} — aggregate inventory counts
 *         by status and by asset type, with optional date-range filtering.</li>
 *     <li>{@link #getActiveAlerts(int, int, String)} — paginated listing of
 *         active (unresolved) system alerts.</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final AssetRepository assetRepository;
    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;

    public DashboardService(AssetRepository assetRepository,
                            AlertRepository alertRepository,
                            AlertMapper alertMapper) {
        this.assetRepository = assetRepository;
        this.alertRepository = alertRepository;
        this.alertMapper = alertMapper;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // §2.4.1 / §2.4.2 — Inventory Summary  (GET /api/v1/dashboard/summary)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns aggregate asset counts broken down by status and by type.
     *
     * <p>When {@code from} and/or {@code to} are supplied, only assets whose
     * {@code purchaseDate} falls within that range are included.  This allows
     * the frontend to render time-scoped charts for usage statistics.
     *
     * @param from optional start of the date range (inclusive), or {@code null}
     * @param to   optional end of the date range (inclusive), or {@code null}
     * @return the dashboard summary DTO
     */
    public DashboardSummaryDTO getSummary(LocalDate from, LocalDate to) {
        Specification<Asset> spec = buildDateRangeSpec(from, to);
        List<Asset> assets = assetRepository.findAll(spec);

        long totalAssets = assets.size();

        // Count by status — all Status enum values
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Status s : Status.values()) {
            long count = assets.stream()
                    .filter(a -> s.equals(a.getStatus()))
                    .count();
            byStatus.put(s.name(), count);
        }

        // Count by type (whatever distinct types exist in the result set)
        Map<String, Long> byType = new LinkedHashMap<>();
        assets.stream()
                .filter(a -> a.getType() != null)
                .map(Asset::getType)
                .distinct()
                .sorted()
                .forEach(type -> {
                    long count = assets.stream()
                            .filter(a -> type.equals(a.getType()))
                            .count();
                    byType.put(type, count);
                });

        // Count by assigned user
        Map<String, Long> byAssignedUser = assets.stream()
                .filter(a -> a.getAssignedUser() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getAssignedUser().getFirstName() + " " + a.getAssignedUser().getLastName(),
                        Collectors.counting()
                ));

        DashboardSummaryDTO dto = new DashboardSummaryDTO();
        dto.setTotalAssets(totalAssets);
        dto.setByStatus(byStatus);
        dto.setByType(byType);
        dto.setByAssignedUser(byAssignedUser);
        return dto;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // §2.4.3 — Active Alerts  (GET /api/v1/alerts)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated list of active (unresolved) system alerts.
     *
     * @param page 1-based page number
     * @param size page size
     * @param type optional {@link AlertType} name filter (e.g., "WARRANTY_EXPIRING")
     * @return page of alert DTOs
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Page<AlertDTO> getActiveAlerts(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("createdAt").descending());

        Page<Alert> alertPage;
        if (type != null && !type.isBlank()) {
            AlertType alertType = AlertType.valueOf(type.toUpperCase());
            alertPage = alertRepository.findByTypeAndResolvedFalse(alertType, pageable);
        } else {
            alertPage = alertRepository.findByResolvedFalse(pageable);
        }

        return alertPage.map(alertMapper::toDto);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private Helpers
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Builds a Specification that optionally restricts assets by purchase date range.
     * If both {@code from} and {@code to} are null, the specification matches all assets.
     */
    private Specification<Asset> buildDateRangeSpec(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchaseDate"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("purchaseDate"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
