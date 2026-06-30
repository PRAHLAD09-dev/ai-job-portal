package com.prahlad.aijobportal.recruiterservice.company.controller;

import com.prahlad.aijobportal.recruiterservice.company.dto.request.CreateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.request.UpdateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyPublicResponse;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyResponse;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyService;
import com.prahlad.aijobportal.recruiterservice.security.principal.AuthenticatedUser;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/companies")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Company registration, profile, statistics, and public profile")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "Register a new company, creating the authenticated user's recruiter profile as its owner")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(principal.userId(), bearerToken, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company registered successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated recruiter's own company profile")
    public ResponseEntity<ApiResponse<CompanyResponse>> getMyCompany(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        CompanyResponse response = companyService.getMyCompany(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    @Operation(summary = "Update the authenticated recruiter's own company profile")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateMyCompany(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse response = companyService.updateMyCompany(principal.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Company updated successfully", response));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete the authenticated recruiter's own company profile")
    public ResponseEntity<ApiResponse<Void>> deleteMyCompany(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        companyService.deleteMyCompany(principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Company deleted successfully", null));
    }

    @GetMapping("/me/statistics")
    @Operation(summary = "Get dashboard statistics for the authenticated recruiter's own company")
    public ResponseEntity<ApiResponse<CompanyStatisticsResponse>> getMyCompanyStatistics(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        CompanyStatisticsResponse response = companyService.getMyCompanyStatistics(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{slug}/public")
    @Operation(summary = "Get a company's public profile by slug (no authentication required)")
    public ResponseEntity<ApiResponse<CompanyPublicResponse>> getPublicProfile(@PathVariable String slug) {
        CompanyPublicResponse response = companyService.getPublicProfile(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
