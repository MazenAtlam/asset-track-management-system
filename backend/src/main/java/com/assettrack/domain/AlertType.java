package com.assettrack.domain;

/**
 * Classifies a system-generated {@link Alert}.
 *
 * <ul>
 *     <li>{@link #WARRANTY_EXPIRING} — An asset's warranty is expiring within the configured lead-time window.</li>
 *     <li>{@link #LOW_STOCK}         — Available inventory for a given asset type has dropped below the threshold.</li>
 * </ul>
 *
 * @see Alert
 */
public enum AlertType {

    /** An asset's warranty is expiring within the configured lead-time window. */
    WARRANTY_EXPIRING,

    /** Available inventory for an asset type has fallen below the configured threshold. */
    LOW_STOCK
}
