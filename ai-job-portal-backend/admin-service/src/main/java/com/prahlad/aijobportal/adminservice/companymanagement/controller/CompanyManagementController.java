package com.prahlad.aijobportal.adminservice.companymanagement.controller;

import com.prahlad.aijobportal.adminservice.companymanagement.service.CompanyManagementService;
import com.prahlad.aijobportal.adminservice.feign.dto.CompanyResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Admin Service's Company Management feature (DAY09_ADMIN_SERVICE.md).
 * Every operation here is a thin wrapper over Recruiter Service's
 * internal admin endpoints via {@code CompanyManagementService}.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/companies")
@RequiredArgsConstructor
@Tag(name = "Admin - Company Management")
public class CompanyManagementController {

    private final CompanyManagementService companyManagementService;

    @GetMapping
    @Operation(summary = "List/search/filter companies")
    public ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> searchCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(companyManagementService.searchCompanies(keyword, status, page, size)));
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "View a single company")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(ApiResponse.success(companyManagementService.getCompany(companyId)));
    }

    @PatchMapping("/{companyId}/verify")
    @Operation(summary = "Verify a company")
    public ResponseEntity<ApiResponse<CompanyResponse>> verifyCompany(@PathVariable UUID companyId,
                                                                       @AuthenticationPrincipal AuthenticatedUser admin,
                                                                       HttpServletRequest request) {
        CompanyResponse company = companyManagementService.verifyCompany(companyId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Company verified successfully", company));
    }

    @PatchMapping("/{companyId}/reject")
    @Operation(summary = "Reject a company")
    public ResponseEntity<ApiResponse<CompanyResponse>> rejectCompany(@PathVariable UUID companyId,
                                                                       @AuthenticationPrincipal AuthenticatedUser admin,
                                                                       HttpServletRequest request) {
        CompanyResponse company = companyManagementService.rejectCompany(companyId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Company rejected successfully", company));
    }

    @PatchMapping("/{companyId}/suspend")
    @Operation(summary = "Suspend a company")
    public ResponseEntity<ApiResponse<CompanyResponse>> suspendCompany(@PathVariable UUID companyId,
                                                                        @AuthenticationPrincipal AuthenticatedUser admin,
                                                                        HttpServletRequest request) {
        CompanyResponse company = companyManagementService.suspendCompany(companyId, admin, request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Company suspended successfully", company));
    }
}
