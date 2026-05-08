package com.assettrack.domain;

/**
 * Enum representing the lifecycle status of an {@link Asset} within the AssetTrack system.
 *
 * <p>Each constant reflects a distinct stage in the asset's lifecycle:
 * <ul>
 *     <li>{@link #AVAILABLE} — The asset is in inventory and ready to be assigned.</li>
 *     <li>{@link #ASSIGNED} — The asset is currently allocated to a user.</li>
 *     <li>{@link #DECOMMISSIONED} — The asset has been retired and is no longer in active service.</li>
 *     <li>{@link #SPARE} — The asset is held in reserve as a backup or replacement unit.</li>
 * </ul>
 *
 * <p>This enum is persisted as a {@code VARCHAR} column (via {@code @Enumerated(EnumType.STRING)})
 * in the {@code assets} table to ensure database readability and safe schema evolution.
 *
 * @see Asset
 */
public enum Status {

    /** The asset is in inventory and ready to be assigned to a user. */
    AVAILABLE,

    /** The asset is currently allocated to and in use by a specific user. */
    ASSIGNED,

    /** The asset has been permanently retired from active service. */
    DECOMMISSIONED,

    /** The asset is held in reserve as a backup or replacement unit. */
    SPARE
}
