package com.prahlad.aijobportal.aiservice.event.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CandidateRankedEvent(
        UUID jobId,
        UUID companyId,
        List<UUID> rankedCandidateIds,
        Instant rankedAt
) {
}
