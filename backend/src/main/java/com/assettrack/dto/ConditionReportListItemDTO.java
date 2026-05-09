package com.assettrack.dto;

import java.time.LocalDateTime;

/**
 * Represents one entry in the paginated condition-report list returned by
 * {@code GET /api/v1/reports}.
 */
public class ConditionReportListItemDTO {

    private Long id;
    private Long assetId;
    private String assetName;        // e.g. "Dell XPS 15"
    private String reporterName;
    private String description;
    private String status;           // ReportStatus string value
    private LocalDateTime submittedAt;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
