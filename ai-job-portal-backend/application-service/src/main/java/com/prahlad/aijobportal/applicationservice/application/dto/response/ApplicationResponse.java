package com.prahlad.aijobportal.applicationservice.application.dto.response;

import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Full application detail view, returned by application detail
 * endpoints on both the candidate and recruiter sides.
 */
public record ApplicationResponse(
        UUID id,
        UUID candidateId,
        String candidateName,
        String candidateEmail,
        UUID recruiterId,
        UUID companyId,
        String companyName,
        UUID jobId,
        String jobTitle,
        String resumeUrl,
        String coverLetter,
        ApplicationStatus status,
        Instant appliedAt,
        Instant interviewDate,
        String notes,
        Instant withdrawnAt,
        Instant createdAt,
        Instant updatedAt
) {
}
