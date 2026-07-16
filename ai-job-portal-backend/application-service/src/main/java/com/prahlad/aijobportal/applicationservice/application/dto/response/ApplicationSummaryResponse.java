package com.prahlad.aijobportal.applicationservice.application.dto.response;

import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight list-view projection, returned by list/search endpoints
 * (candidate "My Applications", recruiter application list) to avoid
 * shipping full detail (cover letter, notes) for every row.
 */
public record ApplicationSummaryResponse(
        UUID id,
        UUID candidateId,
        String candidateName,
        UUID jobId,
        String jobTitle,
        String companyName,
        ApplicationStatus status,
        Instant appliedAt,
        Instant interviewDate,
        boolean viewed
) {
}
