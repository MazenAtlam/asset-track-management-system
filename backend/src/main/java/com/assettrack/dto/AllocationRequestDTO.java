package com.assettrack.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for {@code POST /api/v1/assets/{id}/allocate}.
 */
public class AllocationRequestDTO {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "allocationDate is required")
    private LocalDate allocationDate;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getAllocationDate() { return allocationDate; }
    public void setAllocationDate(LocalDate allocationDate) { this.allocationDate = allocationDate; }
}
