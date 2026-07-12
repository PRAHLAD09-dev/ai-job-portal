package com.prahlad.aijobportal.aiservice.external;

import com.prahlad.aijobportal.aiservice.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.aiservice.feign.CandidateServiceClient;
import com.prahlad.aijobportal.aiservice.feign.dto.CandidateProfileSummaryResponse;
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
public class CandidateLookupService {

    private final CandidateServiceClient candidateServiceClient;

    @CircuitBreaker(name = "aiService", fallbackMethod = "fetchCurrentCandidateFallback")
    @Retry(name = "aiService")
    public CandidateProfileSummaryResponse fetchCurrentCandidate(String bearerToken) {
        ApiResponse<CandidateProfileSummaryResponse> response = candidateServiceClient.getCurrentCandidateProfile(bearerToken);
        return response.getData();
    }

    @SuppressWarnings("unused")
    private CandidateProfileSummaryResponse fetchCurrentCandidateFallback(String bearerToken, Throwable throwable) {
        if (throwable instanceof BusinessException businessException) {
            throw businessException;
        }
        log.error("Candidate Service unreachable", throwable);
        throw new DependencyServiceUnavailableException("Candidate Service is temporarily unavailable. Please try again later.");
    }
}
