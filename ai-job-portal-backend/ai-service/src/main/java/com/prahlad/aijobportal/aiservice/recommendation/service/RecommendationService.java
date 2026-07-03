package com.prahlad.aijobportal.aiservice.recommendation.service;

import com.prahlad.aijobportal.aiservice.recommendation.dto.response.CandidateRecommendationResponse;
import com.prahlad.aijobportal.aiservice.recommendation.dto.response.JobRecommendationResponse;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {

    /** Recommends open jobs to the given candidate, ranked by AI-assessed fit. */
    List<JobRecommendationResponse> recommendJobs(UUID candidateId, String bearerToken);

    /** Ranks applicants of {@code jobId} for the recruiter's review, scoped to their own company (DAY07_AI_SERVICE.md Security). */
    List<CandidateRecommendationResponse> recommendCandidates(UUID recruiterUserId, String bearerToken, UUID jobId);
}
