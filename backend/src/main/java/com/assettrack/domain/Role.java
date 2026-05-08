package com.assettrack.domain;

/**
 * Enum representing the authorization roles available within the AssetTrack system.
 *
 * <p>Each role defines a level of access and operational permissions:
 * <ul>
 *     <li>{@link #ADMIN} — Full system access including user management and configuration.</li>
 *     <li>{@link #MANAGER} — Can manage assets, allocations, and view reports.</li>
 *     <li>{@link #DEVELOPER} — Standard user who can view assigned assets and submit condition reports.</li>
 * </ul>
 *
 * <p>This enum is persisted as a {@code VARCHAR} column (via {@code @Enumerated(EnumType.STRING)})
 * in the {@code users} table to ensure database readability and safe schema evolution.
 *
 * @see User
 */
public enum Role {

    /** Full administrative privileges — user management, system configuration, and all asset operations. */
    ADMIN,

    /** Mid-level privileges — asset lifecycle management, allocation oversight, and reporting. */
    MANAGER,

    /** Standard privileges — view own assigned assets and submit condition reports. */
    DEVELOPER
}
