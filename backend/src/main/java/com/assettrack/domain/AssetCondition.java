package com.assettrack.domain;

/**
 * Describes the physical condition of an asset, either at the time of return
 * or as reported by the current user.
 *
 * <ul>
 *     <li>{@link #GOOD}  — Asset is fully functional with no visible damage.</li>
 *     <li>{@link #FAIR}  — Asset works but shows signs of wear or minor issues.</li>
 *     <li>{@link #POOR}  — Asset has significant damage or performance problems.</li>
 * </ul>
 *
 * @see AllocationHistory
 * @see ConditionReport
 */
public enum AssetCondition {

    /** Fully functional, no visible damage. */
    GOOD,

    /** Works but shows wear or minor issues. */
    FAIR,

    /** Significant damage or serious performance problems. */
    POOR
}
