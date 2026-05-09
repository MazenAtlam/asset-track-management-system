package com.assettrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for {@code POST /api/v1/assets/{id}/reports} (Submit Condition Report).
 */
public class ConditionReportRequestDTO {

    /**
     * The category of the issue (e.g., "HARDWARE", "SOFTWARE", "OTHER").
     * Mapped to {@link com.assettrack.domain.IssueType}.
     */
    @NotBlank(message = "issueType is required")
    private String issueType;

    @NotBlank(message = "description is required")
    private String description;

    /**
     * The reporter's assessment of the asset's current condition
     * (e.g., "GOOD", "FAIR", "POOR").
     * Mapped to {@link com.assettrack.domain.AssetCondition}.
     */
    @NotBlank(message = "condition is required")
    private String condition;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
