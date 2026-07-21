package com.prahlad.aijobportal.aiservice.interviewprep.controller;

import com.prahlad.aijobportal.aiservice.interviewprep.dto.request.GenerateInterviewPrepRequest;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.DetectedTopicsResponse;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.InterviewPrepQuestionSetResponse;
import com.prahlad.aijobportal.aiservice.interviewprep.service.InterviewPrepService;
import com.prahlad.aijobportal.aiservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Candidate-facing AI Interview Generator endpoints, per the AI
 * Interview Generator PRD. Distinct from
 * {@link com.prahlad.aijobportal.aiservice.interview.controller.InterviewQuestionController},
 * which generates recruiter-facing interview questions from a job
 * posting - this generates candidate-facing practice questions from
 * their own resume. Security rule "Candidate can only access their own
 * practice questions" is enforced structurally: {@code candidateId} is
 * always derived from the authenticated JWT principal, never from a
 * request parameter.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/interview-prep")
@RequiredArgsConstructor
@Tag(name = "AI Interview Prep", description = "Resume-based interview practice question generator")
public class InterviewPrepController {

    private final InterviewPrepService interviewPrepService;

    @GetMapping("/topics")
    @Operation(summary = "Detect practiceable topics (skills and projects) from the authenticated candidate's latest resume")
    public ResponseEntity<ApiResponse<DetectedTopicsResponse>> detectTopics(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        DetectedTopicsResponse response = interviewPrepService.detectTopics(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate a fresh set of resume-based interview practice questions",
            description = "Also used for \"Regenerate\": resubmit the same request to get a different set of questions.")
    public ResponseEntity<ApiResponse<InterviewPrepQuestionSetResponse>> generate(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody GenerateInterviewPrepRequest request) {
        InterviewPrepQuestionSetResponse response = interviewPrepService.generate(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview questions generated successfully", response));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the authenticated candidate's most recently generated practice question set")
    public ResponseEntity<ApiResponse<InterviewPrepQuestionSetResponse>> getLatest(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        InterviewPrepQuestionSetResponse response = interviewPrepService.getLatest(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{questionSetId}/pdf")
    @Operation(summary = "Download a previously generated question set as a PDF")
    public ResponseEntity<byte[]> downloadPdf(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID questionSetId) {
        byte[] pdf = interviewPrepService.generatePdf(principal.userId(), questionSetId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ai-interview-questions.pdf\"")
                .body(pdf);
    }
}
