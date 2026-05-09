package com.assettrack.dto;

import java.util.Map;

/**
 * Response DTO for {@code GET /api/v1/dashboard/summary}.
 *
 * <p>Provides aggregate counts used to populate dashboard charts.
 */
public class DashboardSummaryDTO {

    private long totalAssets;

    /**
     * Asset counts keyed by {@link com.assettrack.domain.Status} string value.
     * Example: {@code {"AVAILABLE": 20, "ASSIGNED": 120, ...}}
     */
    private Map<String, Long> byStatus;

    /**
     * Asset counts keyed by type string value.
     * Example: {@code {"LAPTOP": 50, "MONITOR": 60, ...}}
     */
    private Map<String, Long> byType;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public long getTotalAssets() { return totalAssets; }
    public void setTotalAssets(long totalAssets) { this.totalAssets = totalAssets; }

    public Map<String, Long> getByStatus() { return byStatus; }
    public void setByStatus(Map<String, Long> byStatus) { this.byStatus = byStatus; }

    public Map<String, Long> getByType() { return byType; }
    public void setByType(Map<String, Long> byType) { this.byType = byType; }
}
