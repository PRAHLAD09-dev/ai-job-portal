package com.prahlad.aijobportal.aiservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record ResumeAnalyzedEvent(
        UUID resumeAnalysisId,
        UUID candidateId,
        UUID candidateUserId,
        int atsScore,
        Instant analyzedAt
) {
}
