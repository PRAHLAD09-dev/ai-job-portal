package com.prahlad.aijobportal.candidateservice.feign.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Mirrors the shape of Auth Service's {@code UserResponse} DTO, as
 * returned (wrapped in {@code ApiResponse}) by {@code GET /api/v1/auth/me}.
 * Kept as its own DTO (rather than importing Auth Service's class) since
 * microservices must not share compiled DTOs across module boundaries —
 * each service owns its own contract for what it consumes from another.
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
