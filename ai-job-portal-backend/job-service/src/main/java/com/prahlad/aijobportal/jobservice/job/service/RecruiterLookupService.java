package com.prahlad.aijobportal.jobservice.job.service;

import com.prahlad.aijobportal.jobservice.feign.RecruiterServiceClient;
import com.prahlad.aijobportal.jobservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.jobservice.job.exception.AuthServiceUnavailableException;
import com.prahlad.aijobportal.common.exception.BusinessException;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Wraps the synchronous Feign call to Recruiter Service with a circuit
 * breaker and retry, per DECISIONS.md (Resilience4j: Circuit Breaker,
 * Retry, Timeout). Kept as its own Spring bean (rather than a private
 * method on a service impl) so that Spring AOP can actually intercept
 * the call through the proxy — self-invocation within the same class
 * bypasses Spring's annotation-driven proxies.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterLookupService {

    private final RecruiterServiceClient recruiterServiceClient;

    @CircuitBreaker(name = "jobService", fallbackMethod = "fetchCurrentRecruiterFallback")
    @Retry(name = "jobService")
    public RecruiterSummaryResponse fetchCurrentRecruiter(String bearerToken) {
        ApiResponse<RecruiterSummaryResponse> response = recruiterServiceClient.getCurrentRecruiter(bearerToken);
        return response.getData();
    }

    @SuppressWarnings("unused")
    private RecruiterSummaryResponse fetchCurrentRecruiterFallback(String bearerToken, Throwable throwable) {
        // fallbackMethod fires for EVERY exception the guarded call throws,
        // including ones listed in resilience4j's ignoreExceptions — that
        // config only excludes an exception from the failure-rate/open-state
        // calculation, it does not skip the fallback. A 404 "this recruiter
        // hasn't finished onboarding yet" is a legitimate business outcome,
        // not Recruiter Service being down, so it must be rethrown as-is
        // rather than masked as a fake 503.
        if (throwable instanceof BusinessException businessException) {
            throw businessException;
        }
        log.error("Recruiter Service unreachable while fetching current recruiter", throwable);
        throw new AuthServiceUnavailableException("Recruiter Service is temporarily unavailable. Please try again later.");
    }
}
