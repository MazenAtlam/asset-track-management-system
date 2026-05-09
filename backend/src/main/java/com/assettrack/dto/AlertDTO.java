package com.assettrack.dto;

/**
 * Represents a single alert item in the paginated alerts response for
 * {@code GET /api/v1/alerts}.
 */
public class AlertDTO {

    private Long alertId;
    private String type;
    private Long assetId;  // null for LOW_STOCK alerts
    private String message;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
