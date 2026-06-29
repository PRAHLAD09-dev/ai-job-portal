package com.prahlad.aijobportal.candidateservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published to the {@code candidate-profile-updated} Kafka topic whenever
 * a candidate's profile (or any sub-resource affecting profile
 * completion) changes. Consumed downstream in a later phase — this
 * service only publishes; it does not implement a consumer.
 */
public record CandidateProfileUpdatedEvent(
        UUID candidateId,
        UUID userId,
        int profileCompletionPercentage,
        Instant updatedAt
) {
}
