package com.assettrack.domain;

import java.time.LocalDateTime;

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
 * JPA entity representing a condition or issue report submitted by a user
 * for one of their currently assigned assets.
 *
 * <p>Reports are always initiated by the asset's current holder and are
 * visible to Admins and Managers for follow-up. The lifecycle of a report
 * is tracked via {@link ReportStatus}.
 *
 * <p>Maps to the {@code condition_reports} table in PostgreSQL.
 *
 * @see Asset
 * @see ReportStatus
 * @see IssueType
 * @see AssetCondition
 */
@Entity
@Table(name = "condition_reports")
public class ConditionReport {

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
     * The asset this report is filed against.
     * LAZY fetch to avoid loading the full asset graph on report list queries.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    /**
     * The user who submitted this report (typically the current asset holder).
     * LAZY for the same performance reason as {@link #asset}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // ──────────────────────────────────────────────────────────────────────────────
    // Report Content
    // ──────────────────────────────────────────────────────────────────────────────

    /** The category of issue being reported. */
    @Enumerated(EnumType.STRING)
    @Column(name = "issue_type", nullable = false)
    private IssueType issueType;

    /** A free-text description of the issue provided by the reporting user. */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** The reporter's assessment of the asset's overall physical condition. */
    @Enumerated(EnumType.STRING)
    @Column(name = "condition")
    private AssetCondition condition;

    // ──────────────────────────────────────────────────────────────────────────────
    // Status & Timestamps
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * The current follow-up status of this report.
     * Defaults to {@link ReportStatus#PENDING} on creation.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    /** The UTC timestamp at which this report was submitted. */
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    // ──────────────────────────────────────────────────────────────────────────────
    // Constructors
    // ──────────────────────────────────────────────────────────────────────────────

    /** No-arg constructor required by JPA. */
    public ConditionReport() {
    }

    /**
     * Convenience constructor for creating a new, PENDING condition report.
     *
     * @param asset       the asset being reported on
     * @param reporter    the user filing the report
     * @param issueType   the category of the issue
     * @param description the user's free-text description
     * @param condition   the reporter's assessment of asset condition
     * @param submittedAt the time the report was submitted
     */
    public ConditionReport(Asset asset, User reporter, IssueType issueType,
                           String description, AssetCondition condition, LocalDateTime submittedAt) {
        this.asset = asset;
        this.reporter = reporter;
        this.issueType = issueType;
        this.description = description;
        this.condition = condition;
        this.submittedAt = submittedAt;
        this.status = ReportStatus.PENDING;
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

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AssetCondition getCondition() {
        return condition;
    }

    public void setCondition(AssetCondition condition) {
        this.condition = condition;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Object Overrides
    // ──────────────────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "ConditionReport{" +
                "id=" + id +
                ", assetId=" + (asset != null ? asset.getId() : null) +
                ", reporterId=" + (reporter != null ? reporter.getId() : null) +
                ", issueType=" + issueType +
                ", condition=" + condition +
                ", status=" + status +
                ", submittedAt=" + submittedAt +
                '}';
    }
}
