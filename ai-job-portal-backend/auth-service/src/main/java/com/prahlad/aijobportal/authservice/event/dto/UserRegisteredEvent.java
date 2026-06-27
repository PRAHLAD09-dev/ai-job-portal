package com.prahlad.aijobportal.authservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published to the {@code user-registered} Kafka topic when a new account
 * is created. Consumed downstream (e.g. by Notification Service in a later
 * phase) — this service only publishes; it does not implement consumers.
 */
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String role,
        Instant registeredAt
) {
}
