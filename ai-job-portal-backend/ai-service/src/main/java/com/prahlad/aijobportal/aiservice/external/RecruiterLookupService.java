package com.prahlad.aijobportal.aiservice.external;

import com.prahlad.aijobportal.aiservice.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.aiservice.feign.RecruiterServiceClient;
import com.prahlad.aijobportal.aiservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.common.exception.BusinessException;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterLookupService {

    private final RecruiterServiceClient recruiterServiceClient;

    @CircuitBreaker(name = "aiService", fallbackMethod = "fetchCurrentRecruiterFallback")
    @Retry(name = "aiService")
    public RecruiterSummaryResponse fetchCurrentRecruiter(String bearerToken) {
        ApiResponse<RecruiterSummaryResponse> response = recruiterServiceClient.getCurrentRecruiter(bearerToken);
        return response.getData();
    }

    @SuppressWarnings("unused")
    private RecruiterSummaryResponse fetchCurrentRecruiterFallback(String bearerToken, Throwable throwable) {
        if (throwable instanceof BusinessException businessException) {
            throw businessException;
        }
        log.error("Recruiter Service unreachable", throwable);
        throw new DependencyServiceUnavailableException("Recruiter Service is temporarily unavailable. Please try again later.");
    }
}
