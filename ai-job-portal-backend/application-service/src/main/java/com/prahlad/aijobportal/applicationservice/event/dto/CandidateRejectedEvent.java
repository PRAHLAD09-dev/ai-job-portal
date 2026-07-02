package com.prahlad.aijobportal.applicationservice.event.dto;

import java.time.Instant;
import java.util.UUID;

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
