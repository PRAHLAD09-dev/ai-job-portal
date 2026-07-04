package com.prahlad.aijobportal.notificationservice.notification.dto.response;

import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationStatus;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String title,
        String message,
        NotificationType type,
        NotificationStatus status,
        boolean read,
        Instant createdAt
) {
}
