package com.prahlad.aijobportal.jobservice.feign.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Mirrors the shape of Auth Service's {@code UserResponse} DTO, as
 * returned (wrapped in {@code ApiResponse}) by {@code GET /api/v1/auth/me}.
 */
public record UserSummaryResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        String status,
        boolean emailVerified,
        Instant createdAt
) {
}
