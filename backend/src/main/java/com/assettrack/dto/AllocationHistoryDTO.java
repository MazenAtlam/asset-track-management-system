package com.assettrack.dto;

import java.time.LocalDate;

/**
 * Represents one entry in the paginated allocation history list returned by
 * {@code GET /api/v1/assets/{id}/history}.
 */
public class AllocationHistoryDTO {

    private Long historyId;
    private Long userId;
    private String userName;
    private LocalDate assignedDate;
    private LocalDate returnedDate;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }

    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }
}
