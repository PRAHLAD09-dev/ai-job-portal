package com.prahlad.aijobportal.candidateservice.candidate.controller;

import com.prahlad.aijobportal.candidateservice.candidate.dto.request.CreateCandidateProfileRequest;
import com.prahlad.aijobportal.candidateservice.candidate.dto.request.UpdateCandidateProfileRequest;
import com.prahlad.aijobportal.candidateservice.candidate.dto.response.CandidateProfileResponse;
import com.prahlad.aijobportal.candidateservice.candidate.dto.response.ProfileCompletionResponse;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateService;
import com.prahlad.aijobportal.candidateservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/candidate/profile")
@RequiredArgsConstructor
@Tag(name = "Candidate Profile", description = "Candidate profile creation, retrieval, update, and deletion")
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping
    @Operation(summary = "Create the authenticated user's candidate profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> createProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @Valid @RequestBody CreateCandidateProfileRequest request) {
        CandidateProfileResponse response = candidateService.createProfile(principal.userId(), bearerToken, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Candidate profile created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get the authenticated user's candidate profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getProfile(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        CandidateProfileResponse response = candidateService.getProfile(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @Operation(summary = "Update the authenticated user's candidate profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> updateProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody UpdateCandidateProfileRequest request) {
        CandidateProfileResponse response = candidateService.updateProfile(principal.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile updated successfully", response));
    }

    @DeleteMapping
    @Operation(summary = "Delete the authenticated user's candidate profile")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        candidateService.deleteProfile(principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Candidate profile deleted successfully", null));
    }

    @GetMapping("/completion")
    @Operation(summary = "Get the authenticated user's profile completion percentage")
    public ResponseEntity<ApiResponse<ProfileCompletionResponse>> getProfileCompletion(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        ProfileCompletionResponse response = candidateService.getProfileCompletion(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
