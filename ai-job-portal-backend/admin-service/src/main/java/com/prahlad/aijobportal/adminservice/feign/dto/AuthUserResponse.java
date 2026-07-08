package com.prahlad.aijobportal.adminservice.feign.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Mirrors the shape of Auth Service's {@code AdminUserResponse} DTO, as
 * returned (wrapped in {@code ApiResponse}) by
 * {@code GET /api/v1/auth/internal/admin/users/**}. Admin Service
 * declares its own copy of the fields it needs rather than sharing a
 * DTO class across module boundaries, so a producer-side field addition
 * never breaks this client.
 */
public record AuthUserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        String status,
        boolean emailVerified,
        boolean accountLocked,
        int failedLoginAttempts,
        Instant createdAt,
        Instant updatedAt
) {
}
