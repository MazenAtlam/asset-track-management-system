package com.assettrack.dto;

/**
 * Response DTO for {@code GET /api/v1/assets/actions/spare-laptop}.
 *
 * <p>Returns the first available spare laptop, including the last known
 * owner pulled from allocation history.
 */
public class SpareLaptopDTO {

    private Long id;
    private String brand;
    private String model;
    private String status;

    /**
     * The most recent user who held this laptop before it became spare,
     * or {@code null} if it was never previously assigned.
     */
    private LastOwnerDTO lastOwner;

    // ── Nested DTO ────────────────────────────────────────────────────────────

    public static class LastOwnerDTO {
        private Long id;
        private String name;

        public LastOwnerDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LastOwnerDTO getLastOwner() { return lastOwner; }
    public void setLastOwner(LastOwnerDTO lastOwner) { this.lastOwner = lastOwner; }
}
