package com.prahlad.aijobportal.candidateservice.candidate.service;

import com.prahlad.aijobportal.candidateservice.candidate.exception.AuthServiceUnavailableException;
import com.prahlad.aijobportal.candidateservice.feign.AuthServiceClient;
import com.prahlad.aijobportal.candidateservice.feign.dto.UserSummaryResponse;
import com.prahlad.aijobportal.common.exception.BusinessException;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Wraps the synchronous Feign call to Auth Service with a circuit
 * breaker and retry, per DECISIONS.md (Resilience4j: Circuit Breaker,
 * Retry, Timeout). Kept as its own Spring bean (rather than a private
 * method on {@code CandidateServiceImpl}) so that Spring AOP can
 * actually intercept the call through the proxy — self-invocation
 * within the same class bypasses Spring's annotation-driven proxies.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUserLookupService {

    private final AuthServiceClient authServiceClient;

    @CircuitBreaker(name = "candidateService", fallbackMethod = "fetchCurrentUserFallback")
    @Retry(name = "candidateService")
    public UserSummaryResponse fetchCurrentUser(String bearerToken) {
        ApiResponse<UserSummaryResponse> authResponse = authServiceClient.getCurrentUser(bearerToken);
        return authResponse.getData();
    }

    @SuppressWarnings("unused")
    private UserSummaryResponse fetchCurrentUserFallback(String bearerToken, Throwable throwable) {
        if (throwable instanceof BusinessException businessException) {
            throw businessException;
        }
        log.error("Auth Service unreachable while fetching current user", throwable);
        throw new AuthServiceUnavailableException("Auth Service is temporarily unavailable. Please try again later.");
    }
}
