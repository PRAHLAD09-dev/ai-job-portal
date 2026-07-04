package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of AI Service ResumeAnalyzedEvent (topic resume-analyzed). */
public record ResumeAnalyzedEvent(
        UUID resumeAnalysisId,
        UUID candidateId,
        UUID candidateUserId,
        int atsScore,
        Instant analyzedAt
) {
}
