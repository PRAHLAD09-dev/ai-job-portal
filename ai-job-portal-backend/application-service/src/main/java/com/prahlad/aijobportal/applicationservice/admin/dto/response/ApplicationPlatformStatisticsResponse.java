package com.prahlad.aijobportal.applicationservice.admin.dto.response;

/**
 * Platform-wide application counts, consumed by Admin Service's
 * Application Monitoring feature (Platform Application Statistics), per
 * DAY09_ADMIN_SERVICE.md.
 */
public record ApplicationPlatformStatisticsResponse(
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
