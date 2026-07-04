package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Application Service CandidateRejectedEvent (topic candidate-rejected). */
public record CandidateRejectedEvent(
        UUID applicationId,
        UUID jobId,
        UUID candidateId,
        UUID candidateUserId,
        String jobTitle,
        String remarks,
        Instant rejectedAt
) {
}
