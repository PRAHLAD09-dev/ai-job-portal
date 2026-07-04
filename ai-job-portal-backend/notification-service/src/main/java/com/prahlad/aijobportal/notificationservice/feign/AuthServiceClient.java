package com.prahlad.aijobportal.notificationservice.feign;

import com.prahlad.aijobportal.notificationservice.feign.dto.UserSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Synchronous Feign client to Auth Service, used to resolve a
 * recipient's e-mail address and display name from the {@code userId}
 * carried by every consumed Kafka event. Unlike every other Feign
 * client in the platform, this one is called from a Kafka listener with
 * no incoming HTTP request, so there is no bearer token to forward.
 * Instead it authenticates via {@link FeignClientConfig}'s internal
 * service token interceptor against Auth Service's internal-only
 * endpoint (never routed through the API Gateway).
 */
@FeignClient(name = "AUTH-SERVICE", path = CommonConstants.API_BASE_PATH + "/auth/internal", configuration = FeignClientConfig.class)
public interface AuthServiceClient {

    @GetMapping("/users/{userId}")
    ApiResponse<UserSummaryResponse> getUserById(@PathVariable("userId") UUID userId);
}
