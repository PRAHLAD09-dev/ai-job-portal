package com.prahlad.aijobportal.adminservice.feign.dto;

/**
 * Mirrors the shape of Application Service's
 * {@code ApplicationPlatformStatisticsResponse} DTO, as returned by
 * {@code GET /api/v1/applications/internal/admin/statistics}.
 */
public record ApplicationStatisticsResponse(
        long totalApplications,
        long appliedCount,
        long underReviewCount,
        long shortlistedCount,
        long interviewCount,
        long offeredCount,
        long hiredCount,
        long rejectedCount,
        long withdrawnCount
) {
}
