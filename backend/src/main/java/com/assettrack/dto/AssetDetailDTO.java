package com.assettrack.dto;

import java.time.LocalDate;

/**
 * Full asset detail response for {@code GET /api/v1/assets/{id}}.
 * Includes all fields (dates, assignedUser) not present in the list view.
 */
public class AssetDetailDTO {

    private Long id;
    private String type;
    private String brand;
    private String model;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpirationDate;
    private String status;

    /** Currently assigned user, or {@code null} when unassigned. */
    private AssignedUserDTO assignedUser;

    // ── Nested DTO ────────────────────────────────────────────────────────────

    public static class AssignedUserDTO {
        private Long id;
        private String name;

        public AssignedUserDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getWarrantyExpirationDate() { return warrantyExpirationDate; }
    public void setWarrantyExpirationDate(LocalDate d) { this.warrantyExpirationDate = d; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public AssignedUserDTO getAssignedUser() { return assignedUser; }
    public void setAssignedUser(AssignedUserDTO assignedUser) { this.assignedUser = assignedUser; }
}
