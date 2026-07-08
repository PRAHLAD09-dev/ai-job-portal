package com.prahlad.aijobportal.applicationservice.admin.controller;

import com.prahlad.aijobportal.applicationservice.admin.dto.response.ApplicationPlatformStatisticsResponse;
import com.prahlad.aijobportal.applicationservice.admin.service.AdminApplicationService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal-only, service-to-service endpoint backing Admin Service's
 * Application Monitoring feature (DAY09_ADMIN_SERVICE.md). Never routed
 * through the API Gateway and never callable with a normal user bearer
 * token — authenticated exclusively by {@code InternalServiceAuthFilter}
 * via the shared {@code X-Internal-Service-Token} header. Read-only;
 * existing candidate/recruiter-facing application endpoints are
 * untouched.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/applications/internal/admin")
@RequiredArgsConstructor
@Tag(name = "Internal - Admin", description = "Service-to-service endpoints for Admin Service, not exposed through the API Gateway")
public class InternalAdminApplicationController {

    private final AdminApplicationService adminApplicationService;

    @GetMapping("/statistics")
    @Operation(summary = "Get platform-wide application statistics (internal callers only)")
    public ResponseEntity<ApiResponse<ApplicationPlatformStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(adminApplicationService.getPlatformStatistics()));
    }
}
