package com.prahlad.aijobportal.aiservice.event.consumed;

import java.time.Instant;
import java.util.UUID;

/**
 * Local mirror of Application Service's {@code ApplicationCreatedEvent}
 * (topic {@code application-created}). Services never share event DTO
 * classes across module boundaries — each consumer declares its own
 * copy of the fields it actually needs, deserialized manually (see
 * {@link com.prahlad.aijobportal.aiservice.event.AiEventConsumer}) so a
 * producer-side class rename can never break this consumer.
 */
public record ApplicationCreatedEvent(
        UUID applicationId,
        UUID jobId,
        UUID candidateId,
        UUID candidateUserId,
        UUID companyId,
        String jobTitle,
        Instant appliedAt
) {
}
