package com.prahlad.aijobportal.aiservice.coverletter.controller;

import com.prahlad.aijobportal.aiservice.coverletter.dto.request.CoverLetterRequest;
import com.prahlad.aijobportal.aiservice.coverletter.dto.response.CoverLetterResponse;
import com.prahlad.aijobportal.aiservice.coverletter.service.CoverLetterService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/ai/cover-letter")
@RequiredArgsConstructor
@Tag(name = "AI Cover Letter", description = "AI-generated cover letters for candidates")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    @PostMapping
    @Operation(summary = "Generate a cover letter for the authenticated candidate for a given job")
    public ResponseEntity<ApiResponse<CoverLetterResponse>> generate(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @Valid @RequestBody CoverLetterRequest request) {
        CoverLetterResponse response = coverLetterService.generate(bearerToken, request);
        return ResponseEntity.ok(ApiResponse.success("Cover letter generated successfully", response));
    }
}
