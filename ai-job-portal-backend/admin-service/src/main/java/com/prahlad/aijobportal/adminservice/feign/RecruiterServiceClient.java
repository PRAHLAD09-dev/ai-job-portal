package com.prahlad.aijobportal.adminservice.feign;

import com.prahlad.aijobportal.adminservice.feign.dto.CompanyResponse;
import com.prahlad.aijobportal.adminservice.feign.dto.CompanyStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Synchronous Feign client to Recruiter Service's internal-only admin
 * endpoints (never routed through the API Gateway). Authenticates via
 * {@link FeignClientConfig}'s internal-service-token interceptor. Used
 * exclusively to back Admin Service's Company Management and Dashboard
 * features (DAY09_ADMIN_SERVICE.md); never duplicates Recruiter
 * Service's own business logic.
 */
@FeignClient(name = "RECRUITER-SERVICE", url = "${services.recruiter-service.url:}", path = CommonConstants.API_BASE_PATH + "/companies/internal/admin", configuration = FeignClientConfig.class)
public interface RecruiterServiceClient {

    @GetMapping("/companies")
    ApiResponse<PageResponse<CompanyResponse>> searchCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam int page,
            @RequestParam int size);

    @GetMapping("/companies/statistics")
    ApiResponse<CompanyStatisticsResponse> getStatistics();

    @GetMapping("/companies/{companyId}")
    ApiResponse<CompanyResponse> getCompany(@PathVariable("companyId") UUID companyId);

    @PatchMapping("/companies/{companyId}/verify")
    ApiResponse<CompanyResponse> verifyCompany(@PathVariable("companyId") UUID companyId);

    @PatchMapping("/companies/{companyId}/reject")
    ApiResponse<CompanyResponse> rejectCompany(@PathVariable("companyId") UUID companyId);

    @PatchMapping("/companies/{companyId}/suspend")
    ApiResponse<CompanyResponse> suspendCompany(@PathVariable("companyId") UUID companyId);
}
