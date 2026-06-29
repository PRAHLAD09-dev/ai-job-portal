package com.prahlad.aijobportal.candidateservice.event.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Published to the {@code resume-uploaded} Kafka topic whenever a
 * candidate uploads a new resume. Consumed downstream in a later phase
 * (e.g. by AI Service to trigger resume analysis) — this service only
 * publishes; it does not implement a consumer.
 */
public record ResumeUploadedEvent(
        UUID candidateId,
        UUID userId,
        UUID resumeId,
        String fileUrl,
        int versionNumber,
        Instant uploadedAt
) {
}
