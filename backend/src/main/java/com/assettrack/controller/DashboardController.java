package com.assettrack.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assettrack.dto.AlertDTO;
import com.assettrack.dto.DashboardSummaryDTO;
import com.assettrack.service.DashboardService;

/**
 * REST controller for §2.4 Reporting & Analytics endpoints.
 *
 * <p>Exposes:
 * <ul>
 *     <li>{@code GET /api/v1/dashboard/summary} — aggregate inventory counts for charts.</li>
 *     <li>{@code GET /api/v1/alerts} — paginated list of active system alerts.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/v1/dashboard/summary — Inventory Summary
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns aggregate asset statistics for the dashboard.
     *
     * <p>Supports optional date-range filtering on {@code purchaseDate} so
     * the frontend can display time-scoped usage statistics.
     *
     * @param from optional start date (ISO 8601 format: YYYY-MM-DD)
     * @param to   optional end date   (ISO 8601 format: YYYY-MM-DD)
     * @return 200 OK with {@link DashboardSummaryDTO}
     */
    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(dashboardService.getSummary(from, to));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/v1/alerts — Get Active Alerts
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated list of active (unresolved) alerts.
     *
     * @param page 1-based page number (default 1)
     * @param size records per page (default 10)
     * @param type optional alert type filter (e.g., "WARRANTY_EXPIRING")
     * @return 200 OK with paged alerts list
     */
    @GetMapping("/alerts")
    public ResponseEntity<Map<String, Object>> getActiveAlerts(
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {

        Page<AlertDTO> result = dashboardService.getActiveAlerts(page, size, type);
        return ResponseEntity.ok(Map.of(
                "totalItems",  result.getTotalElements(),
                "totalPages",  result.getTotalPages(),
                "currentPage", page,
                "alerts",      result.getContent()
        ));
    }
}
