package com.prahlad.aijobportal.aiservice.interview.controller;

import com.prahlad.aijobportal.aiservice.interview.dto.request.GenerateInterviewQuestionsRequest;
import com.prahlad.aijobportal.aiservice.interview.dto.response.InterviewQuestionResponse;
import com.prahlad.aijobportal.aiservice.interview.service.InterviewQuestionService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/interview")
@RequiredArgsConstructor
@Tag(name = "AI Interview Questions", description = "AI-generated interview questions per job")
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;

    @PostMapping("/questions")
    @Operation(summary = "Generate interview questions for a job owned by the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<List<InterviewQuestionResponse>>> generate(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @Valid @RequestBody GenerateInterviewQuestionsRequest request) {
        List<InterviewQuestionResponse> response =
                interviewQuestionService.generate(principal.userId(), bearerToken, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview questions generated successfully", response));
    }

    @GetMapping("/questions/{jobId}")
    @Operation(summary = "Get previously generated interview questions for a job")
    public ResponseEntity<ApiResponse<List<InterviewQuestionResponse>>> getForJob(@PathVariable UUID jobId) {
        List<InterviewQuestionResponse> response = interviewQuestionService.getForJob(jobId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
