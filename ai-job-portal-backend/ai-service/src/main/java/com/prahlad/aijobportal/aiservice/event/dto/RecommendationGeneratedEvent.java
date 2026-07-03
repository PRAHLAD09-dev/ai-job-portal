package com.prahlad.aijobportal.aiservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record RecommendationGeneratedEvent(
        UUID candidateId,
        UUID candidateUserId,
        int recommendationCount,
        Instant generatedAt
) {
}
