package com.prahlad.aijobportal.notificationservice.notification.enums;

/**
 * Delivery lifecycle status of a
 * com.prahlad.aijobportal.notificationservice.notification.entity.Notification.
 * READ is set independently of delivery outcome; a notification
 * that failed to send by email can still be marked read once seen
 * in-app.
 */
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    READ
}
