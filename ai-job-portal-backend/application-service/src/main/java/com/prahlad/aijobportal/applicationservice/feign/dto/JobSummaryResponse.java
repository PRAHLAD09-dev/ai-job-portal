package com.prahlad.aijobportal.applicationservice.feign.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Minimal projection of Job Service's {@code JobResponse}, as returned
 * (wrapped in {@code ApiResponse}) by the PUBLIC
 * {@code GET /api/v1/jobs/{jobId}} endpoint. Only the fields Application
 * Service actually needs to validate and denormalize an application are
 * declared here — extra fields present in the real response are simply
 * ignored by Jackson at deserialization time.
 */
public record JobSummaryResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String title,
        String status,
        Instant applicationDeadline,
        String applyMethod,
        String externalApplyUrl
) {
}
