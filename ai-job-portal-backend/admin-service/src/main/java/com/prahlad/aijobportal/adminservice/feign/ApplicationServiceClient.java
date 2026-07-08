package com.prahlad.aijobportal.adminservice.feign;

import com.prahlad.aijobportal.adminservice.feign.dto.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Synchronous Feign client to Application Service's internal-only admin
 * statistics endpoint (never routed through the API Gateway).
 * Authenticates via {@link FeignClientConfig}'s internal-service-token
 * interceptor. Used exclusively to back Admin Service's Application
 * Monitoring feature (DAY09_ADMIN_SERVICE.md); read-only.
 */
@FeignClient(name = "APPLICATION-SERVICE", path = CommonConstants.API_BASE_PATH + "/applications/internal/admin", configuration = FeignClientConfig.class)
public interface ApplicationServiceClient {

    @GetMapping("/statistics")
    ApiResponse<ApplicationStatisticsResponse> getStatistics();
}
