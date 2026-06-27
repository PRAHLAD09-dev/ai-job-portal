package com.prahlad.aijobportal.authservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published to the {@code password-reset-requested} Kafka topic when a
 * user requests a password reset. Consumed downstream (e.g. by
 * Notification Service in a later phase) — this service only publishes.
 */
public record PasswordResetRequestedEvent(
        UUID userId,
        String email,
        String firstName,
        Instant requestedAt
) {
}
