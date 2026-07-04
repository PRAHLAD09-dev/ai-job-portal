package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of AI Service RecommendationGeneratedEvent (topic recommendation-generated). */
public record RecommendationGeneratedEvent(
        UUID candidateId,
        UUID candidateUserId,
        int recommendationCount,
        Instant generatedAt
) {
}
