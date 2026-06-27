package com.prahlad.aijobportal.authservice.user.enums;

/**
 * Lifecycle status of a {@code User} account.
 */
public enum AccountStatus {
    /** Registered but email not yet verified. Login is blocked. */
    PENDING_VERIFICATION,
    /** Email verified, account fully usable. */
    ACTIVE,
    /** Disabled by an administrator. Login is blocked. */
    DISABLED
}
