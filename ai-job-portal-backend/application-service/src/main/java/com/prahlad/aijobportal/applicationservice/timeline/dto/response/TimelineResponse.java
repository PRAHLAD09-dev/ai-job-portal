package com.prahlad.aijobportal.applicationservice.timeline.dto.response;

import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

public record TimelineResponse(
        UUID id,
        UUID applicationId,
        ApplicationStatus oldStatus,
        ApplicationStatus newStatus,
        UUID changedBy,
        Instant changedAt,
        String remarks
) {
}
