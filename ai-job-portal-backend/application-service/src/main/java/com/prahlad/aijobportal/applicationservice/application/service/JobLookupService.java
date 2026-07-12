package com.prahlad.aijobportal.applicationservice.application.service;

import com.prahlad.aijobportal.applicationservice.application.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.applicationservice.feign.JobServiceClient;
import com.prahlad.aijobportal.applicationservice.feign.dto.JobSummaryResponse;
import com.prahlad.aijobportal.common.exception.BusinessException;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Wraps the synchronous Feign call to Job Service (public endpoint —
 * no bearer token required) with a circuit breaker and retry.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobLookupService {

    private final JobServiceClient jobServiceClient;

    @CircuitBreaker(name = "applicationService", fallbackMethod = "fetchJobFallback")
    @Retry(name = "applicationService")
    public JobSummaryResponse fetchJob(UUID jobId) {
        ApiResponse<JobSummaryResponse> response = jobServiceClient.getJobById(jobId);
        return response.getData();
    }

    @SuppressWarnings("unused")
    private JobSummaryResponse fetchJobFallback(UUID jobId, Throwable throwable) {
        if (throwable instanceof BusinessException businessException) {
            throw businessException;
        }
        log.error("Job Service unreachable while fetching job {}", jobId, throwable);
        throw new DependencyServiceUnavailableException("Job Service is temporarily unavailable. Please try again later.");
    }
}
