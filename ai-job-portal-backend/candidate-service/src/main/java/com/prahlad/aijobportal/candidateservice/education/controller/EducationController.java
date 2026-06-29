package com.prahlad.aijobportal.candidateservice.education.controller;

import com.prahlad.aijobportal.candidateservice.education.dto.request.EducationRequest;
import com.prahlad.aijobportal.candidateservice.education.dto.response.EducationResponse;
import com.prahlad.aijobportal.candidateservice.education.service.EducationService;
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
@RequestMapping(CommonConstants.API_BASE_PATH + "/candidate/education")
@RequiredArgsConstructor
@Tag(name = "Candidate Education", description = "CRUD operations on a candidate's education history")
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    @Operation(summary = "Add an education entry to the authenticated candidate's profile")
    public ResponseEntity<ApiResponse<EducationResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody EducationRequest request) {
        EducationResponse response = educationService.create(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Education entry created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all education entries for the authenticated candidate")
    public ResponseEntity<ApiResponse<List<EducationResponse>>> getAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        List<EducationResponse> response = educationService.getAll(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{educationId}")
    @Operation(summary = "Update an education entry owned by the authenticated candidate")
    public ResponseEntity<ApiResponse<EducationResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID educationId,
            @Valid @RequestBody EducationRequest request) {
        EducationResponse response = educationService.update(principal.userId(), educationId, request);
        return ResponseEntity.ok(ApiResponse.success("Education entry updated successfully", response));
    }

    @DeleteMapping("/{educationId}")
    @Operation(summary = "Delete an education entry owned by the authenticated candidate")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID educationId) {
        educationService.delete(principal.userId(), educationId);
        return ResponseEntity.ok(ApiResponse.success("Education entry deleted successfully", null));
    }
}
