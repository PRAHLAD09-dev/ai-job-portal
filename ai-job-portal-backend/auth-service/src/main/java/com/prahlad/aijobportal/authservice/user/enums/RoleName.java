package com.prahlad.aijobportal.authservice.user.enums;

/**
 * Platform-wide role names, per PROJECT_SPECIFICATION.md Section 2 (User Roles).
 * Persisted as the {@code name} column of the {@code roles} table and used
 * as Spring Security authorities (prefixed with {@code ROLE_}).
 */
public enum RoleName {
    CANDIDATE,
    RECRUITER,
    ADMIN,
    /**
     * Added for Admin Service (DAY09_ADMIN_SERVICE.md): a super-privileged
     * administrator tier above {@code ADMIN}. Purely additive — existing
     * ADMIN accounts and authorization checks are unaffected.
     */
    SUPER_ADMIN
}
