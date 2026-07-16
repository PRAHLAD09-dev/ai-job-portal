package com.prahlad.aijobportal.aiservice.resumeanalysis.controller;

import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ResumeAnalysisResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ResumeAnalysisService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * DAY11 Recruiter Dashboard "AI Match": internal-only, service-to-service
 * endpoint letting Recruiter Service look up an arbitrary candidate's
 * latest resume analysis (ATS score) for the dashboard. Never routed
 * through the API Gateway and never callable with a normal user bearer
 * token — authenticated exclusively by {@code InternalServiceAuthFilter}
 * via the shared {@code X-Internal-Service-Token} header, mirroring the
 * identical pattern already established for Admin Service in
 * {@code InternalAdminAiController}.
 *
 * This intentionally reuses {@link ResumeAnalysisService#getLatestForCandidate(UUID)}
 * as-is (the same method backing the candidate-facing
 * {@code GET /ai/resume/analyze/latest}) rather than duplicating any
 * scoring logic — only the candidate-id source changes (path variable
 * here vs. the authenticated principal there).
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/internal/recruiter")
@RequiredArgsConstructor
@Tag(name = "Internal - Recruiter", description = "Service-to-service endpoints for Recruiter Service, not exposed through the API Gateway")
public class InternalRecruiterAiController {

    private final ResumeAnalysisService resumeAnalysisService;

    @GetMapping("/resume-analysis/{candidateId}")
    @Operation(summary = "Get a candidate's latest resume analysis / ATS score (internal callers only)")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> getLatestForCandidate(@PathVariable UUID candidateId) {
        return ResponseEntity.ok(ApiResponse.success(resumeAnalysisService.getLatestForCandidate(candidateId)));
    }
}
