package com.prahlad.aijobportal.aiservice.recommendation.service;

import com.prahlad.aijobportal.aiservice.recommendation.dto.response.CandidateRecommendationResponse;
import com.prahlad.aijobportal.aiservice.recommendation.dto.response.JobRecommendationResponse;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {

    /** Recommends open jobs to the given candidate, ranked by AI-assessed fit. */
    List<JobRecommendationResponse> recommendJobs(UUID candidateId, String bearerToken);

    /**
     * Returns the candidate's already-computed match score for a single
     * job, for display on that job's detail page. Reads the persisted
     * {@code job_recommendations} row from the candidate's last
     * {@link #recommendJobs} run rather than calling the AI provider
     * again - throws {@code ResourceNotFoundException} if that job
     * wasn't part of (or predates) the candidate's last recommendation
     * run, so the caller can prompt them to refresh recommendations.
     */
    JobRecommendationResponse getMatchForJob(UUID candidateId, UUID jobId);

    /** Ranks applicants of {@code jobId} for the recruiter's review, scoped to their own company (DAY07_AI_SERVICE.md Security). */
    List<CandidateRecommendationResponse> recommendCandidates(UUID recruiterUserId, String bearerToken, UUID jobId);
}
