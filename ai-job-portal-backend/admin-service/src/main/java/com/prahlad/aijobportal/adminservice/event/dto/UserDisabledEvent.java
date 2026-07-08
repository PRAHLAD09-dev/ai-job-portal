package com.prahlad.aijobportal.adminservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when an administrator disables a user account, per
 * DAY09_ADMIN_SERVICE.md's Kafka section. Notification Service (or any
 * future consumer) may subscribe to react to this without Admin Service
 * needing to know who its consumers are.
 */
public record UserDisabledEvent(
        UUID userId,
        String email,
        UUID disabledByAdminId,
        Instant disabledAt
) {
}
