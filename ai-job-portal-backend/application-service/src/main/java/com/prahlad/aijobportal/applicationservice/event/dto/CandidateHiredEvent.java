package com.prahlad.aijobportal.applicationservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record CandidateHiredEvent(
        UUID applicationId,
        UUID jobId,
        UUID candidateId,
        UUID candidateUserId,
        UUID companyId,
        String jobTitle,
        Instant hiredAt
) {
}
