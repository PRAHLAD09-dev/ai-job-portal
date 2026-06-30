package com.prahlad.aijobportal.recruiterservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published to the {@code company-updated} Kafka topic whenever a
 * company's profile changes. Consumed downstream in a later phase — this
 * service only publishes; it does not implement a consumer.
 */
public record CompanyUpdatedEvent(
        UUID companyId,
        String companyName,
        String slug,
        Instant updatedAt
) {
}
