package com.prahlad.aijobportal.aiservice.recommendation.controller;

import com.prahlad.aijobportal.aiservice.recommendation.dto.response.CandidateRecommendationResponse;
import com.prahlad.aijobportal.aiservice.recommendation.dto.response.JobRecommendationResponse;
import com.prahlad.aijobportal.aiservice.recommendation.service.RecommendationService;
import com.prahlad.aijobportal.aiservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * AI job/candidate matching endpoints, per DAY07_AI_SERVICE.md's "AI
 * Endpoints" section. Security: candidates only ever see recommendations
 * for themselves (derived from JWT); recruiters may only rank
 * candidates for jobs owned by their own company (enforced in
 * {@code RecommendationServiceImpl}).
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai")
@RequiredArgsConstructor
@Tag(name = "AI Recommendations", description = "Job matching for candidates and candidate ranking for recruiters")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/jobs/recommend")
    @Operation(summary = "Recommend open jobs to the authenticated candidate")
    public ResponseEntity<ApiResponse<List<JobRecommendationResponse>>> recommendJobs(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken) {
        List<JobRecommendationResponse> response = recommendationService.recommendJobs(principal.userId(), bearerToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/jobs/{jobId}/match")
    @Operation(summary = "Get the authenticated candidate's AI match score for a single job",
            description = "Reads the candidate's already-computed score from their last POST /ai/jobs/recommend "
                    + "run. Returns 404 if that job wasn't part of (or predates) that run - the frontend should "
                    + "treat this as \"no score yet\" and may prompt the candidate to refresh recommendations, "
                    + "rather than treating it as an error.")
    public ResponseEntity<ApiResponse<JobRecommendationResponse>> getMatchForJob(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID jobId) {
        JobRecommendationResponse response = recommendationService.getMatchForJob(principal.userId(), jobId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/candidates/recommend/{jobId}")
    @Operation(summary = "Rank applicants of a job for the authenticated recruiter")
    public ResponseEntity<ApiResponse<List<CandidateRecommendationResponse>>> recommendCandidates(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @PathVariable UUID jobId) {
        List<CandidateRecommendationResponse> response =
                recommendationService.recommendCandidates(principal.userId(), bearerToken, jobId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
