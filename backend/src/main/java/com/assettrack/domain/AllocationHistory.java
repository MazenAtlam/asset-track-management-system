package com.assettrack.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing a single allocation event for an {@link Asset}.
 *
 * <p>Each row records one period of ownership: who held the asset, when they
 * received it, when (if ever) they returned it, and the asset's condition at
 * the time of return. Together these rows form the complete chain of custody
 * for every tracked asset.
 *
 * <p><strong>Open record:</strong> a row with {@code returnedDate == null}
 * represents the <em>current</em> active allocation.
 *
 * <p>Maps to the {@code allocation_history} table in PostgreSQL.
 *
 * @see Asset
 * @see AssetCondition
 */
@Entity
@Table(name = "allocation_history")
public class AllocationHistory {

    // ──────────────────────────────────────────────────────────────────────────────
    // Primary Key
    // ──────────────────────────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // ──────────────────────────────────────────────────────────────────────────────
    // Relationships
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * The asset this allocation record belongs to.
     * LAZY fetch keeps paginated history queries lean — the asset is joined only
     * when explicitly accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    /**
     * The user who held the asset during this allocation period.
     * LAZY for the same reason as {@link #asset}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ──────────────────────────────────────────────────────────────────────────────
    // Allocation Period
    // ──────────────────────────────────────────────────────────────────────────────

    /** The date on which the asset was assigned to {@link #user}. */
    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    /**
     * The date on which the asset was returned.
     * {@code null} while the allocation is still active (i.e., the asset is ASSIGNED).
     */
    @Column(name = "returned_date")
    private LocalDate returnedDate;

    // ──────────────────────────────────────────────────────────────────────────────
    // Return Condition
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * The physical condition of the asset at the time it was returned.
     * {@code null} until the asset is actually returned.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_upon_return")
    private AssetCondition conditionUponReturn;

    // ──────────────────────────────────────────────────────────────────────────────
    // Constructors
    // ──────────────────────────────────────────────────────────────────────────────

    /** No-arg constructor required by JPA. */
    public AllocationHistory() {
    }

    /**
     * Creates an open (active) allocation record — no return date yet.
     *
     * @param asset        the asset being allocated
     * @param user         the user receiving the asset
     * @param assignedDate the date of allocation
     */
    public AllocationHistory(Asset asset, User user, LocalDate assignedDate) {
        this.asset = asset;
        this.user = user;
        this.assignedDate = assignedDate;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Getters & Setters
    // ──────────────────────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public AssetCondition getConditionUponReturn() {
        return conditionUponReturn;
    }

    public void setConditionUponReturn(AssetCondition conditionUponReturn) {
        this.conditionUponReturn = conditionUponReturn;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Object Overrides
    // ──────────────────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "AllocationHistory{" +
                "id=" + id +
                ", assetId=" + (asset != null ? asset.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", assignedDate=" + assignedDate +
                ", returnedDate=" + returnedDate +
                ", conditionUponReturn=" + conditionUponReturn +
                '}';
    }
}
