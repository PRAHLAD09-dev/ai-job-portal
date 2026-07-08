package com.prahlad.aijobportal.recruiterservice.company.enums;

/**
 * Verification lifecycle status of a {@code Company}, per
 * PROJECT_SPECIFICATION.md Section 2 (Admin: Verify companies).
 */
public enum VerificationStatus {
    PENDING,
    VERIFIED,
    REJECTED,
    /**
     * Added for Admin Service (DAY09_ADMIN_SERVICE.md): a previously
     * verified (or pending) company suspended by an administrator.
     * Purely additive — stored in the existing VARCHAR(20) column with
     * no CHECK constraint, so no migration is required.
     */
    SUSPENDED
}
