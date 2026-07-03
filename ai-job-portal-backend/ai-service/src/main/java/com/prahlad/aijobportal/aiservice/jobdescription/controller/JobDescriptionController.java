package com.prahlad.aijobportal.aiservice.jobdescription.controller;

import com.prahlad.aijobportal.aiservice.jobdescription.dto.request.JobDescriptionRequest;
import com.prahlad.aijobportal.aiservice.jobdescription.dto.response.JobDescriptionResponse;
import com.prahlad.aijobportal.aiservice.jobdescription.service.JobDescriptionService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/job-description")
@RequiredArgsConstructor
@Tag(name = "AI Job Description", description = "AI-generated job descriptions for recruiters")
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    @PostMapping
    @Operation(summary = "Draft a job description and required skills from a few key points")
    public ResponseEntity<ApiResponse<JobDescriptionResponse>> generate(@Valid @RequestBody JobDescriptionRequest request) {
        JobDescriptionResponse response = jobDescriptionService.generate(request);
        return ResponseEntity.ok(ApiResponse.success("Job description generated successfully", response));
    }
}
