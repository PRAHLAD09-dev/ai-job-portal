package com.prahlad.aijobportal.adminservice.feign.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Mirrors the shape of Job Service's {@code AdminJobResponse} DTO, as
 * returned (wrapped in {@code ApiResponse}) by
 * {@code GET /api/v1/jobs/internal/admin/jobs/**}.
 */
public record JobResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String title,
        String slug,
        String jobType,
        String workMode,
        String status,
        boolean featured,
        long viewCount,
        Instant publishedAt,
        Instant closedAt,
        Instant createdAt
) {
}
