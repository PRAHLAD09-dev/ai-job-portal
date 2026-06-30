package com.prahlad.aijobportal.recruiterservice.location.controller;

import com.prahlad.aijobportal.recruiterservice.location.dto.request.CompanyLocationRequest;
import com.prahlad.aijobportal.recruiterservice.location.dto.response.CompanyLocationResponse;
import com.prahlad.aijobportal.recruiterservice.location.service.CompanyLocationService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/companies/me/locations")
@RequiredArgsConstructor
@Tag(name = "Company Locations", description = "CRUD operations on the authenticated recruiter's company office locations")
public class CompanyLocationController {

    private final CompanyLocationService companyLocationService;

    @PostMapping
    @Operation(summary = "Add a location to the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<CompanyLocationResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CompanyLocationRequest request) {
        CompanyLocationResponse response = companyLocationService.create(principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Location added successfully", response));
    }

    @GetMapping
    @Operation(summary = "List all locations for the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<List<CompanyLocationResponse>>> getAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        List<CompanyLocationResponse> response = companyLocationService.getAll(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{locationId}")
    @Operation(summary = "Update a location belonging to the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<CompanyLocationResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID locationId,
            @Valid @RequestBody CompanyLocationRequest request) {
        CompanyLocationResponse response = companyLocationService.update(principal.userId(), locationId, request);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", response));
    }

    @DeleteMapping("/{locationId}")
    @Operation(summary = "Delete a location belonging to the authenticated recruiter's company")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID locationId) {
        companyLocationService.delete(principal.userId(), locationId);
        return ResponseEntity.ok(ApiResponse.success("Location deleted successfully", null));
    }
}
