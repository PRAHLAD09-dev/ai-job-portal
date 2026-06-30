package com.prahlad.aijobportal.recruiterservice.recruiter.controller;

import com.prahlad.aijobportal.recruiterservice.recruiter.dto.request.UpdateRecruiterProfileRequest;
import com.prahlad.aijobportal.recruiterservice.recruiter.dto.response.RecruiterResponse;
import com.prahlad.aijobportal.recruiterservice.recruiter.service.RecruiterService;
import com.prahlad.aijobportal.recruiterservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/recruiter/profile")
@RequiredArgsConstructor
@Tag(name = "Recruiter Profile", description = "Recruiter profile retrieval and update")
public class RecruiterController {

    private final RecruiterService recruiterService;

    @GetMapping
    @Operation(summary = "Get the authenticated user's recruiter profile")
    public ResponseEntity<ApiResponse<RecruiterResponse>> getMyProfile(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        RecruiterResponse response = recruiterService.getMyProfile(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @Operation(summary = "Update the authenticated user's recruiter profile")
    public ResponseEntity<ApiResponse<RecruiterResponse>> updateMyProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody UpdateRecruiterProfileRequest request) {
        RecruiterResponse response = recruiterService.updateMyProfile(principal.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Recruiter profile updated successfully", response));
    }
}
