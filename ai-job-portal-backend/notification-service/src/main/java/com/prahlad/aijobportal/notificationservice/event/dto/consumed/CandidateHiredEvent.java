package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Application Service CandidateHiredEvent (topic candidate-hired). */
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
