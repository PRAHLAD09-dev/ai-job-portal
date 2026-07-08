package com.prahlad.aijobportal.recruiterservice.admin.controller;

import com.prahlad.aijobportal.recruiterservice.admin.dto.response.AdminCompanyResponse;
import com.prahlad.aijobportal.recruiterservice.admin.dto.response.CompanyPlatformStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.admin.service.AdminCompanyService;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal-only, service-to-service endpoints backing Admin Service's
 * Company Management and Dashboard features (DAY09_ADMIN_SERVICE.md).
 * Never routed through the API Gateway and never callable with a normal
 * user bearer token — authenticated exclusively by
 * {@code InternalServiceAuthFilter} via the shared
 * {@code X-Internal-Service-Token} header. Reuses {@code AdminCompanyService}
 * (backed by the same {@code Company} entity/table) rather than
 * duplicating any Recruiter Service business logic; existing recruiter-
 * facing {@code CompanyController} endpoints are untouched.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/companies/internal/admin")
@RequiredArgsConstructor
@Tag(name = "Internal - Admin", description = "Service-to-service endpoints for Admin Service, not exposed through the API Gateway")
public class InternalAdminCompanyController {

    private final AdminCompanyService adminCompanyService;

    @GetMapping("/companies")
    @Operation(summary = "List/search/filter companies (internal callers only)")
    public ResponseEntity<ApiResponse<PageResponse<AdminCompanyResponse>>> searchCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) VerificationStatus status,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<AdminCompanyResponse> page = adminCompanyService.searchCompanies(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/companies/statistics")
    @Operation(summary = "Get platform-wide company statistics (internal callers only)")
    public ResponseEntity<ApiResponse<CompanyPlatformStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(adminCompanyService.getPlatformStatistics()));
    }

    @GetMapping("/companies/{companyId}")
    @Operation(summary = "View a single company's admin profile (internal callers only)")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> getCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(ApiResponse.success(adminCompanyService.getCompany(companyId)));
    }

    @PatchMapping("/companies/{companyId}/verify")
    @Operation(summary = "Verify a company (internal callers only)")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> verifyCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(ApiResponse.success("Company verified successfully", adminCompanyService.verifyCompany(companyId)));
    }

    @PatchMapping("/companies/{companyId}/reject")
    @Operation(summary = "Reject a company (internal callers only)")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> rejectCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(ApiResponse.success("Company rejected successfully", adminCompanyService.rejectCompany(companyId)));
    }

    @PatchMapping("/companies/{companyId}/suspend")
    @Operation(summary = "Suspend a company (internal callers only)")
    public ResponseEntity<ApiResponse<AdminCompanyResponse>> suspendCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(ApiResponse.success("Company suspended successfully", adminCompanyService.suspendCompany(companyId)));
    }
}
