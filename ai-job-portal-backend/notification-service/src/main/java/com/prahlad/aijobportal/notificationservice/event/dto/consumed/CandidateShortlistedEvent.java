package com.prahlad.aijobportal.notificationservice.event.dto.consumed;

import java.time.Instant;
import java.util.UUID;

/** Local mirror of Application Service CandidateShortlistedEvent (topic candidate-shortlisted). */
public record CandidateShortlistedEvent(
        UUID applicationId,
        UUID jobId,
        UUID candidateId,
        UUID candidateUserId,
        String jobTitle,
        Instant shortlistedAt
) {
}
