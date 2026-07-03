package com.prahlad.aijobportal.aiservice.event.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Candidate Service's {@code ResumeUploadedEvent} (topic {@code resume-uploaded}). */
public record ResumeUploadedEvent(
        UUID candidateId,
        UUID userId,
        UUID resumeId,
        String fileUrl,
        int versionNumber,
        Instant uploadedAt
) {
}
