package com.prahlad.aijobportal.authservice.admin.dto.response;

/**
 * Platform-wide user counts, consumed by Admin Service's Dashboard feature
 * (Total Users / Total Recruiters / Total Candidates), per
 * DAY09_ADMIN_SERVICE.md.
 */
public record UserPlatformStatisticsResponse(
        long totalUsers,
        long totalCandidates,
        long totalRecruiters,
        long totalAdmins,
        long activeUsers,
        long disabledUsers,
        long pendingVerificationUsers
) {
}
