package com.prahlad.aijobportal.adminservice.aimonitoring.controller;

import com.prahlad.aijobportal.adminservice.feign.AiServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.AiStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Service's AI Monitoring feature (DAY09_ADMIN_SERVICE.md):
 * read-only AI Usage Statistics, fetched live from AI Service via
 * {@code AiServiceClient}.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/ai")
@RequiredArgsConstructor
@Tag(name = "Admin - AI Monitoring")
public class AiMonitoringController {

    private final AiServiceClient aiServiceClient;

    @GetMapping("/statistics")
    @Operation(summary = "Get platform-wide AI usage statistics")
    @CircuitBreaker(name = "aiService")
    @Retry(name = "aiService")
    public ResponseEntity<ApiResponse<AiStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(aiServiceClient.getStatistics().getData()));
    }
}
