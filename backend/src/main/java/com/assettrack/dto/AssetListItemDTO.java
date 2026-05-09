package com.assettrack.dto;

/**
 * Lightweight asset projection used in the paginated list response
 * for {@code GET /api/v1/assets}.
 *
 * <p>Omits date fields (purchaseDate, warrantyExpirationDate) to keep
 * list payloads lean; those are returned by the detail endpoint instead.
 */
public class AssetListItemDTO {

    private Long id;
    private String type;
    private String brand;
    private String model;
    private String serialNumber;
    private String status;

    /** Minimal representation of the currently assigned user, or {@code null}. */
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public AssignedUserDTO getAssignedUser() { return assignedUser; }
    public void setAssignedUser(AssignedUserDTO assignedUser) { this.assignedUser = assignedUser; }
}
