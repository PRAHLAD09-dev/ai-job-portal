package com.prahlad.aijobportal.aiservice.event.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Candidate Service's {@code CandidateProfileUpdatedEvent} (topic {@code candidate-profile-updated}). */
public record CandidateProfileUpdatedEvent(
        UUID candidateId,
        UUID userId,
        int profileCompletionPercentage,
        Instant updatedAt
) {
}
