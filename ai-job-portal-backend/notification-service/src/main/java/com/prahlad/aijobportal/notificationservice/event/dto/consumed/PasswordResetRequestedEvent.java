package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Auth Service PasswordResetRequestedEvent (topic password-reset-requested). */
public record PasswordResetRequestedEvent(
        UUID userId,
        String email,
        String firstName,
        Instant requestedAt
) {
}
