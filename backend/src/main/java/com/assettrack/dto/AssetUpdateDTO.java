package com.assettrack.dto;

/**
 * Request body for {@code PUT /api/v1/assets/{id}} (Update Asset Details / Status).
 *
 * <p>All fields are optional — only non-null values are applied during the update
 * (partial-update / PATCH semantics on a PUT endpoint).
 */
public class AssetUpdateDTO {

    /** New lifecycle status (e.g., "DECOMMISSIONED"). {@code null} = no change. */
    private String status;

    /** Admin/Manager notes accompanying a status change. {@code null} = no change. */
    private String notes;

    /** New type value. {@code null} = no change. */
    private String type;

    /** New brand value. {@code null} = no change. */
    private String brand;

    /** New model value. {@code null} = no change. */
    private String model;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
