package com.prahlad.aijobportal.applicationservice.application.service;

import com.prahlad.aijobportal.applicationservice.application.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.applicationservice.feign.CandidateServiceClient;
import com.prahlad.aijobportal.applicationservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Wraps the synchronous Feign call to Candidate Service with a circuit
 * breaker and retry, per DECISIONS.md (Resilience4j).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateLookupService {

    private final CandidateServiceClient candidateServiceClient;

    @CircuitBreaker(name = "applicationService", fallbackMethod = "fetchCurrentCandidateFallback")
    @Retry(name = "applicationService")
    public CandidateProfileSummaryResponse fetchCurrentCandidate(String bearerToken) {
        ApiResponse<CandidateProfileSummaryResponse> response = candidateServiceClient.getCurrentCandidateProfile(bearerToken);
        return response.getData();
    }

    @SuppressWarnings("unused")
    private CandidateProfileSummaryResponse fetchCurrentCandidateFallback(String bearerToken, Throwable throwable) {
        log.error("Candidate Service unreachable while fetching current candidate profile", throwable);
        throw new DependencyServiceUnavailableException("Candidate Service is temporarily unavailable. Please try again later.");
    }
}
