package com.prahlad.aijobportal.notificationservice.notification.dto.request;

import jakarta.validation.constraints.NotNull;

public record NotificationPreferenceRequest(
        @NotNull(message = "emailEnabled is required") Boolean emailEnabled,
        @NotNull(message = "pushEnabled is required") Boolean pushEnabled,
        @NotNull(message = "inAppEnabled is required") Boolean inAppEnabled
) {
}
