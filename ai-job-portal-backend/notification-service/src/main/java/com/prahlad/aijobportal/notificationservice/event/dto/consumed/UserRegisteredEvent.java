package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Auth Service UserRegisteredEvent (topic user-registered). */
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String role,
        Instant registeredAt
) {
}
