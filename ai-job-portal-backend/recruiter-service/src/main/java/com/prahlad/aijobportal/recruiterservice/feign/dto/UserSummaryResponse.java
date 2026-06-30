package com.prahlad.aijobportal.recruiterservice.feign.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Mirrors the shape of Auth Service's {@code UserResponse} DTO, as
 * returned (wrapped in {@code ApiResponse}) by {@code GET /api/v1/auth/me}.
 * Kept as its own DTO since microservices must not share compiled DTOs
 * across module boundaries.
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
