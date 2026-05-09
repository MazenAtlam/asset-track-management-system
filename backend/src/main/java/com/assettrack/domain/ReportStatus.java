package com.assettrack.domain;

/**
 * Tracks the follow-up lifecycle of a {@link ConditionReport}.
 *
 * <ul>
 *     <li>{@link #PENDING}   — Report submitted; awaiting Admin/Manager review.</li>
 *     <li>{@link #RESOLVED}  — Issue has been addressed and closed.</li>
 *     <li>{@link #DISMISSED} — Report acknowledged but no action required.</li>
 * </ul>
 *
 * @see ConditionReport
 */
public enum ReportStatus {

    /** Submitted and awaiting Admin/Manager review. */
    PENDING,

    /** Issue addressed and closed. */
    RESOLVED,

    /** Acknowledged but no action was taken. */
    DISMISSED
}
