package com.prahlad.aijobportal.adminservice.feign.dto;

import java.time.LocalDate;

/**
 * Mirrors the shape of Auth Service's {@code UserGrowthPointResponse}
 * DTO, as returned by {@code GET /api/v1/auth/internal/admin/users/growth}.
 */
public record UserGrowthPointResponse(
        LocalDate date,
        long signupCount
) {
}
