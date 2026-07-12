package com.prahlad.aijobportal.aiservice.external;

import com.prahlad.aijobportal.aiservice.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.aiservice.feign.JobServiceClient;
import com.prahlad.aijobportal.aiservice.feign.dto.JobDetailSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobLiteResponse;
import com.prahlad.aijobportal.common.exception.BusinessException;
import com.prahlad.aijobportal.common.response.ApiResponse;
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
public class JobLookupService {

    private final JobServiceClient jobServiceClient;

    @CircuitBreaker(name = "aiService", fallbackMethod = "fetchJobFallback")
    @Retry(name = "aiService")
    public JobDetailSummaryResponse fetchJob(UUID jobId) {
        ApiResponse<JobDetailSummaryResponse> response = jobServiceClient.getJobById(jobId);
        return response.getData();
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fetchLatestJobsFallback")
    @Retry(name = "aiService")
    public List<JobLiteResponse> fetchLatestJobs() {
        ApiResponse<List<JobLiteResponse>> response = jobServiceClient.getLatestJobs();
        return response.getData();
    }

    @SuppressWarnings("unused")
    private JobDetailSummaryResponse fetchJobFallback(UUID jobId, Throwable throwable) {
        if (throwable instanceof BusinessException businessException) {
            throw businessException;
        }
        log.error("Job Service unreachable while fetching job {}", jobId, throwable);
        throw new DependencyServiceUnavailableException("Job Service is temporarily unavailable. Please try again later.");
    }

    @SuppressWarnings("unused")
    private List<JobLiteResponse> fetchLatestJobsFallback(Throwable throwable) {
        log.error("Job Service unreachable while fetching latest jobs", throwable);
        return List.of();
    }
}
