package com.assettrack.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assettrack.dto.ConditionReportListItemDTO;
import com.assettrack.service.ConditionReportService;

/**
 * REST controller for §2.2 / §2.3.2 Condition Reporting endpoints.
 *
 * <p>The submit report endpoint lives on {@code AssetController}
 * ({@code POST /api/v1/assets/{id}/reports}) because it is scoped to an asset.
 * This controller handles the cross-asset admin listing endpoint.
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ConditionReportService conditionReportService;

    public ReportController(ConditionReportService conditionReportService) {
        this.conditionReportService = conditionReportService;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/v1/reports — List Reports (Admin/Manager)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns a paginated list of all condition reports for Admin/Manager follow-up.
     *
     * @param page      1-based page number (default 1)
     * @param size      records per page (default 10)
     * @param status    filter by report status (e.g., "PENDING")
     * @param issueType filter by issue type (e.g., "HARDWARE")
     * @param assetId   filter to a specific asset
     * @return 200 OK with paged reports
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listReports(
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) Long assetId) {

        Page<ConditionReportListItemDTO> result =
                conditionReportService.listReports(page, size, status, issueType, assetId);

        return ResponseEntity.ok(Map.of(
                "totalItems",  result.getTotalElements(),
                "totalPages",  result.getTotalPages(),
                "currentPage", page,
                "reports",     result.getContent()
        ));
    }
}
