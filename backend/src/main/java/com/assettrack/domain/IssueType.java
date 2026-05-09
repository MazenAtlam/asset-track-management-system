package com.assettrack.domain;

/**
 * Categorises the type of issue described in a {@link ConditionReport}.
 *
 * <ul>
 *     <li>{@link #HARDWARE} — Physical or component-level fault (e.g., broken screen, dead battery).</li>
 *     <li>{@link #SOFTWARE} — OS, driver, or application-level problem.</li>
 *     <li>{@link #OTHER}    — Any issue that does not fit the above categories.</li>
 * </ul>
 *
 * @see ConditionReport
 */
public enum IssueType {

    /** Physical or component-level fault. */
    HARDWARE,

    /** OS, driver, or application-level problem. */
    SOFTWARE,

    /** Any other issue not covered by the above categories. */
    OTHER
}
