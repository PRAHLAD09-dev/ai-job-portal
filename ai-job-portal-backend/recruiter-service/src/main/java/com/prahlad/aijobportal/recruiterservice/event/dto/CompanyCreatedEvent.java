package com.prahlad.aijobportal.recruiterservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published to the {@code company-created} Kafka topic whenever a new
 * company is registered. Consumed downstream in a later phase — this
 * service only publishes; it does not implement a consumer.
 */
public record CompanyCreatedEvent(
        UUID companyId,
        UUID recruiterUserId,
        String companyName,
        String slug,
        Instant createdAt
) {
}
