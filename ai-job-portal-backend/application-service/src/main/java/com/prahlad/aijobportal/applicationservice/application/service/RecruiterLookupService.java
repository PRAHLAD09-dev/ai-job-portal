package com.prahlad.aijobportal.applicationservice.application.service;

import com.prahlad.aijobportal.applicationservice.application.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.applicationservice.feign.RecruiterServiceClient;
import com.prahlad.aijobportal.applicationservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Wraps the synchronous Feign call to Recruiter Service with a circuit
 * breaker and retry, per DECISIONS.md (Resilience4j). Kept as its own
 * Spring bean rather than a private method so Spring AOP can intercept
 * the call through the proxy.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterLookupService {

    private final RecruiterServiceClient recruiterServiceClient;

    @CircuitBreaker(name = "applicationService", fallbackMethod = "fetchCurrentRecruiterFallback")
    @Retry(name = "applicationService")
    public RecruiterSummaryResponse fetchCurrentRecruiter(String bearerToken) {
        ApiResponse<RecruiterSummaryResponse> response = recruiterServiceClient.getCurrentRecruiter(bearerToken);
        return response.getData();
    }

    @SuppressWarnings("unused")
    private RecruiterSummaryResponse fetchCurrentRecruiterFallback(String bearerToken, Throwable throwable) {
        log.error("Recruiter Service unreachable while fetching current recruiter", throwable);
        throw new DependencyServiceUnavailableException("Recruiter Service is temporarily unavailable. Please try again later.");
    }
}
