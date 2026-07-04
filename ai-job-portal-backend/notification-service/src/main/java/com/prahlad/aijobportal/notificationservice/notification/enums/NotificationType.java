package com.prahlad.aijobportal.notificationservice.notification.enums;

/**
 * Category of a {@link com.prahlad.aijobportal.notificationservice.notification.entity.Notification},
 * per DAY08_NOTIFICATION_SERVICE.md Enums section. Drives both display
 * (icon/grouping on the frontend) and which NotificationPreference
 * flag gates delivery.
 */
public enum NotificationType {
    EMAIL,
    SYSTEM,
    JOB,
    APPLICATION,
    INTERVIEW,
    OFFER,
    AI
}
