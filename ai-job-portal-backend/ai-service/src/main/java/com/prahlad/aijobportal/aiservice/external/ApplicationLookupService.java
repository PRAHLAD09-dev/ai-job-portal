package com.prahlad.aijobportal.aiservice.external;

import com.prahlad.aijobportal.aiservice.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.aiservice.feign.ApplicationServiceClient;
import com.prahlad.aijobportal.aiservice.feign.dto.ApplicationSummaryResponse;
import com.prahlad.aijobportal.common.exception.BusinessException;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationLookupService {

    private final ApplicationServiceClient applicationServiceClient;

    @CircuitBreaker(name = "aiService", fallbackMethod = "fetchApplicationsForJobFallback")
    @Retry(name = "aiService")
    public List<ApplicationSummaryResponse> fetchApplicationsForJob(String bearerToken, UUID jobId) {
        ApiResponse<PageResponse<ApplicationSummaryResponse>> response =
                applicationServiceClient.getApplications(bearerToken, jobId, 0, 50);
        PageResponse<ApplicationSummaryResponse> page = response.getData();
        return page == null ? List.of() : page.getContent();
    }

    @SuppressWarnings("unused")
    private List<ApplicationSummaryResponse> fetchApplicationsForJobFallback(String bearerToken, UUID jobId, Throwable throwable) {
        if (throwable instanceof BusinessException businessException) {
            throw businessException;
        }
        log.error("Application Service unreachable while fetching applications for job {}", jobId, throwable);
        throw new DependencyServiceUnavailableException("Application Service is temporarily unavailable. Please try again later.");
    }
}
