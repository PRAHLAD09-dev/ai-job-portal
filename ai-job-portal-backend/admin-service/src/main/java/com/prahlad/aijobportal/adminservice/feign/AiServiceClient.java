package com.prahlad.aijobportal.adminservice.feign;

import com.prahlad.aijobportal.adminservice.feign.dto.AiStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Synchronous Feign client to AI Service's internal-only admin
 * statistics endpoint (never routed through the API Gateway).
 * Authenticates via {@link FeignClientConfig}'s internal-service-token
 * interceptor. Used exclusively to back Admin Service's AI Monitoring
 * feature (DAY09_ADMIN_SERVICE.md); read-only.
 */
@FeignClient(name = "AI-SERVICE", path = CommonConstants.API_BASE_PATH + "/ai/internal/admin", configuration = FeignClientConfig.class)
public interface AiServiceClient {

    @GetMapping("/statistics")
    ApiResponse<AiStatisticsResponse> getStatistics();
}
