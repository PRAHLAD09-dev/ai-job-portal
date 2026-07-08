package com.prahlad.aijobportal.notificationservice.admin.dto.response;

/**
 * Platform-wide notification counts, consumed by Admin Service's
 * Notification Monitoring feature (Notification Statistics), per
 * DAY09_ADMIN_SERVICE.md.
 */
public record NotificationPlatformStatisticsResponse(
        long totalNotifications,
        long pendingCount,
        long sentCount,
        long failedCount,
        long readCount,
        long unreadCount
) {
}
