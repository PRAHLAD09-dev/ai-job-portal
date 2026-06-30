package com.prahlad.aijobportal.recruiterservice.sociallink.controller;

import com.prahlad.aijobportal.recruiterservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.request.CompanySocialLinkRequest;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.response.CompanySocialLinkResponse;
import com.prahlad.aijobportal.recruiterservice.sociallink.service.CompanySocialLinkService;
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
@RequestMapping(CommonConstants.API_BASE_PATH + "/companies/me/social-links")
@RequiredArgsConstructor
@Tag(name = "Company Social Links", description = "CRUD operations on the authenticated recruiter's company social/web links")
public class CompanySocialLinkController {

    private final CompanySocialLinkService companySocialLinkService;

    @PostMapping
    @Operation(summary = "Add a social link to the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<CompanySocialLinkResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CompanySocialLinkRequest request) {
        CompanySocialLinkResponse response = companySocialLinkService.create(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Social link added successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all social links for the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<List<CompanySocialLinkResponse>>> getAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        List<CompanySocialLinkResponse> response = companySocialLinkService.getAll(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{linkId}")
    @Operation(summary = "Update a social link belonging to the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<CompanySocialLinkResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID linkId,
            @Valid @RequestBody CompanySocialLinkRequest request) {
        CompanySocialLinkResponse response = companySocialLinkService.update(principal.userId(), linkId, request);
        return ResponseEntity.ok(ApiResponse.success("Social link updated successfully", response));
    }

    @DeleteMapping("/{linkId}")
    @Operation(summary = "Delete a social link belonging to the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID linkId) {
        companySocialLinkService.delete(principal.userId(), linkId);
        return ResponseEntity.ok(ApiResponse.success("Social link deleted successfully", null));
    }
}
