package com.prahlad.aijobportal.adminservice.feign.dto;

/**
 * Mirrors the shape of Notification Service's
 * {@code NotificationPlatformStatisticsResponse} DTO, as returned by
 * {@code GET /api/v1/notifications/internal/admin/statistics}.
 */
public record NotificationStatisticsResponse(
        long totalNotifications,
        long pendingCount,
        long sentCount,
        long failedCount,
        long readCount,
        long unreadCount
) {
}
