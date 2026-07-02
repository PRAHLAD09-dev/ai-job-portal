package com.prahlad.aijobportal.applicationservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record CandidateShortlistedEvent(
        UUID applicationId,
        UUID jobId,
        UUID candidateId,
        UUID candidateUserId,
        String jobTitle,
        Instant shortlistedAt
) {
}
