package com.assettrack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standardized response payload for asset mutation endpoints (POST, PUT).
 * Provides a lightweight confirmation message instead of returning raw entities.
 * 
 * Uses JsonInclude.Include.NON_NULL to automatically omit null fields (like status on creation)
 * from the JSON response, matching API specifications exactly.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetConfirmationDTO {

    private String message;
    private Long assetId;
    private String status;

    public AssetConfirmationDTO() {
    }

    public AssetConfirmationDTO(String message, Long assetId, String status) {
        this.message = message;
        this.assetId = assetId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
