package com.prahlad.aijobportal.candidateservice.experience.controller;

import com.prahlad.aijobportal.candidateservice.experience.dto.request.ExperienceRequest;
import com.prahlad.aijobportal.candidateservice.experience.dto.response.ExperienceResponse;
import com.prahlad.aijobportal.candidateservice.experience.service.ExperienceService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/candidate/experience")
@RequiredArgsConstructor
@Tag(name = "Candidate Experience", description = "CRUD operations on a candidate's work experience history")
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    @Operation(summary = "Add a work experience entry to the authenticated candidate's profile")
    public ResponseEntity<ApiResponse<ExperienceResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ExperienceRequest request) {
        ExperienceResponse response = experienceService.create(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Experience entry created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all work experience entries for the authenticated candidate")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        List<ExperienceResponse> response = experienceService.getAll(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{experienceId}")
    @Operation(summary = "Update a work experience entry owned by the authenticated candidate")
    public ResponseEntity<ApiResponse<ExperienceResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID experienceId,
            @Valid @RequestBody ExperienceRequest request) {
        ExperienceResponse response = experienceService.update(principal.userId(), experienceId, request);
        return ResponseEntity.ok(ApiResponse.success("Experience entry updated successfully", response));
    }

    @DeleteMapping("/{experienceId}")
    @Operation(summary = "Delete a work experience entry owned by the authenticated candidate")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID experienceId) {
        experienceService.delete(principal.userId(), experienceId);
        return ResponseEntity.ok(ApiResponse.success("Experience entry deleted successfully", null));
    }
}
