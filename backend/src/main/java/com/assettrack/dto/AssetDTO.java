package com.assettrack.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for {@code POST /api/v1/assets} (Register Asset).
 * All fields are required when adding a new asset to the system.
 */
public class AssetDTO {

    @NotBlank(message = "Asset type is required")
    private String type;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;

    @NotNull(message = "Warranty expiration date is required")
    private LocalDate warrantyExpirationDate;

    // ── Getters & Setters ──────────────────────────────────────────────────────

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
    public void setWarrantyExpirationDate(LocalDate warrantyExpirationDate) {
        this.warrantyExpirationDate = warrantyExpirationDate;
    }
}
