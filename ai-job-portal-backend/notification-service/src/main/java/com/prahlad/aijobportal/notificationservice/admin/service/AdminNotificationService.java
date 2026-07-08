package com.prahlad.aijobportal.notificationservice.admin.service;

import com.prahlad.aijobportal.notificationservice.admin.dto.response.NotificationPlatformStatisticsResponse;

/**
 * Backs Notification Service's internal admin statistics endpoint,
 * called exclusively by Admin Service via Feign + the shared
 * internal-service token. Read-only: reuses the existing
 * {@code NotificationRepository} count queries rather than duplicating
 * any Notification Service business logic.
 */
public interface AdminNotificationService {

    NotificationPlatformStatisticsResponse getPlatformStatistics();
}
