package com.prahlad.aijobportal.aiservice.recommendation.dto.response;

import java.util.UUID;

/**
 * A recruiter-facing ranked applicant for one job. Unlike
 * {@link JobRecommendationResponse}, this is never persisted — the
 * applicant pool for a job changes too frequently (new applications
 * arrive continuously) for a stored ranking to stay meaningful, so it
 * is recomputed on each request and only cached briefly (see
 * {@code CANDIDATE_RECOMMENDATIONS_CACHE}).
 */
public record CandidateRecommendationResponse(
        UUID applicationId,
        UUID candidateId,
        String candidateName,
        int matchScore,
        String reasoning
) {
}
