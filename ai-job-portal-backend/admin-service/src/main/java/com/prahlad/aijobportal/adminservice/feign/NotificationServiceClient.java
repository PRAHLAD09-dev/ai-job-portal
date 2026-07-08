package com.prahlad.aijobportal.adminservice.feign;

import com.prahlad.aijobportal.adminservice.feign.dto.NotificationStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Synchronous Feign client to Notification Service's internal-only
 * admin statistics endpoint (never routed through the API Gateway).
 * Authenticates via {@link FeignClientConfig}'s internal-service-token
 * interceptor. Used exclusively to back Admin Service's Notification
 * Monitoring feature (DAY09_ADMIN_SERVICE.md); read-only.
 */
@FeignClient(name = "NOTIFICATION-SERVICE", path = CommonConstants.API_BASE_PATH + "/notifications/internal/admin", configuration = FeignClientConfig.class)
public interface NotificationServiceClient {

    @GetMapping("/statistics")
    ApiResponse<NotificationStatisticsResponse> getStatistics();
}
