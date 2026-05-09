package com.assettrack.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for {@code POST /api/v1/assets/{id}/return}.
 */
public class ReturnRequestDTO {

    @NotNull(message = "returnDate is required")
    private LocalDate returnDate;

    /**
     * The asset's condition at the time of return (e.g., "GOOD", "FAIR", "POOR").
     * Mapped to {@link com.assettrack.domain.AssetCondition}.
     */
    private String conditionUponReturn;

    /**
     * The new status the asset should transition to after return
     * (e.g., "AVAILABLE" or "SPARE"). Defaults to "AVAILABLE" if omitted.
     */
    private String newStatus;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getConditionUponReturn() { return conditionUponReturn; }
    public void setConditionUponReturn(String conditionUponReturn) {
        this.conditionUponReturn = conditionUponReturn;
    }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}
