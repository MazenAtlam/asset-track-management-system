package com.assettrack.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing a system-generated alert surfaced on the dashboard.
 *
 * <p>Alerts are created by the scheduled {@code NotificationService} jobs
 * (e.g., when a warranty is nearing expiry or available stock for an asset
 * type drops below a configured threshold). They are also emailed to the
 * configured notification address via SMTP.
 *
 * <p>An alert is "active" while {@link #resolved} is {@code false}. Admins
 * or Managers may dismiss/resolve alerts through the system.
 *
 * <p>Maps to the {@code alerts} table in PostgreSQL.
 *
 * @see AlertType
 */
@Entity
@Table(name = "alerts")
public class Alert {

    // ──────────────────────────────────────────────────────────────────────────────
    // Primary Key
    // ──────────────────────────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // ──────────────────────────────────────────────────────────────────────────────
    // Alert Content
    // ──────────────────────────────────────────────────────────────────────────────

    /** The category of alert (warranty expiry or low stock). */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AlertType type;

    /**
     * The ID of the asset this alert is related to, if applicable.
     * {@code null} for low-stock alerts that relate to an entire asset type
     * rather than a specific unit.
     */
    @Column(name = "asset_id")
    private Long assetId;

    /**
     * The human-readable alert message displayed in the dashboard and sent
     * as the email body.
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    // ──────────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ──────────────────────────────────────────────────────────────────────────────

    /** The UTC timestamp when this alert was generated. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Whether this alert has been resolved/dismissed.
     * {@code false} = active; {@code true} = resolved/dismissed.
     * Defaults to {@code false} on creation.
     */
    @Column(name = "resolved", nullable = false)
    private boolean resolved = false;

    // ──────────────────────────────────────────────────────────────────────────────
    // Constructors
    // ──────────────────────────────────────────────────────────────────────────────

    /** No-arg constructor required by JPA. */
    public Alert() {
    }

    /**
     * Creates a new unresolved alert with a specific asset reference.
     *
     * @param type      the category of alert
     * @param assetId   the asset this alert refers to (may be {@code null})
     * @param message   the human-readable alert message
     * @param createdAt the generation timestamp (UTC)
     */
    public Alert(AlertType type, Long assetId, String message, LocalDateTime createdAt) {
        this.type = type;
        this.assetId = assetId;
        this.message = message;
        this.createdAt = createdAt;
        this.resolved = false;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Getters & Setters
    // ──────────────────────────────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Object Overrides
    // ──────────────────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", type=" + type +
                ", assetId=" + assetId +
                ", resolved=" + resolved +
                ", createdAt=" + createdAt +
                '}';
    }
}
