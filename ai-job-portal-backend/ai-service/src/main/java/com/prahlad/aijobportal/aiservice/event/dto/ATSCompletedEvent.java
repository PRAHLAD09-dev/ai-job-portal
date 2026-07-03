package com.prahlad.aijobportal.aiservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record ATSCompletedEvent(
        UUID resumeAnalysisId,
        UUID candidateId,
        int atsScore,
        Instant completedAt
) {
}
