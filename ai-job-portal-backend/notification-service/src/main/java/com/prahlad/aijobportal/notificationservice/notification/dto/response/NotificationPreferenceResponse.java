package com.prahlad.aijobportal.notificationservice.notification.dto.response;

import java.util.UUID;

public record NotificationPreferenceResponse(
        UUID id,
        UUID userId,
        boolean emailEnabled,
        boolean pushEnabled,
        boolean inAppEnabled
) {
}
