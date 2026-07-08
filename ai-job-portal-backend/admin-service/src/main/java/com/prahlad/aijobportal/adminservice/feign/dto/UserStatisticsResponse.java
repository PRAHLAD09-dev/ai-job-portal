package com.prahlad.aijobportal.adminservice.feign.dto;

/**
 * Mirrors the shape of Auth Service's
 * {@code UserPlatformStatisticsResponse} DTO, as returned by
 * {@code GET /api/v1/auth/internal/admin/users/statistics}.
 */
public record UserStatisticsResponse(
        long totalUsers,
        long totalCandidates,
        long totalRecruiters,
        long totalAdmins,
        long activeUsers,
        long disabledUsers,
        long pendingVerificationUsers
) {
}
