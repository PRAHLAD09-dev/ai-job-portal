package com.prahlad.aijobportal.aiservice.resumeanalysis.controller;

import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request.AnalyzeResumeRequest;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ATSScoreResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ResumeAnalysisResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ATSScoreService;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ResumeAnalysisService;
import com.prahlad.aijobportal.aiservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Candidate-facing resume AI endpoints, per DAY07_AI_SERVICE.md's "AI
 * Endpoints" section. Security rule "Candidate can analyze only own
 * resume" is enforced by always deriving {@code candidateId} from the
 * authenticated JWT principal — never from a request parameter.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/resume")
@RequiredArgsConstructor
@Tag(name = "AI Resume", description = "Resume analysis and ATS scoring")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;
    private final ATSScoreService atsScoreService;

    @PostMapping("/analyze")
    @Operation(summary = "Analyze the authenticated candidate's resume")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> analyze(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody AnalyzeResumeRequest request) {
        ResumeAnalysisResponse response = resumeAnalysisService.analyze(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume analyzed successfully", response));
    }

    @GetMapping("/analyze/latest")
    @Operation(summary = "Get the authenticated candidate's most recent resume analysis")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> getLatest(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        ResumeAnalysisResponse response = resumeAnalysisService.getLatestForCandidate(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/score")
    @Operation(summary = "Get a quick ATS compatibility score for resume text")
    public ResponseEntity<ApiResponse<ATSScoreResponse>> score(
            @Valid @RequestBody AnalyzeResumeRequest request) {
        ATSScoreResponse response = atsScoreService.score(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
