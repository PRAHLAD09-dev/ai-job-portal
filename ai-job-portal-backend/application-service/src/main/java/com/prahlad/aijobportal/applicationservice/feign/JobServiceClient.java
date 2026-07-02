package com.prahlad.aijobportal.applicationservice.feign;

import com.prahlad.aijobportal.applicationservice.feign.dto.JobSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Synchronous Feign client to Job Service, used to validate a job
 * exists/is open and to resolve its {@code companyId}/{@code title}
 * when a candidate applies. {@code GET /api/v1/jobs/{jobId}} is a
 * PUBLIC endpoint on Job Service, so no bearer token needs to be
 * forwarded.
 */
@FeignClient(name = "JOB-SERVICE", path = CommonConstants.API_BASE_PATH + "/jobs", configuration = FeignClientConfig.class)
public interface JobServiceClient {

    @GetMapping("/{jobId}")
    ApiResponse<JobSummaryResponse> getJobById(@PathVariable("jobId") UUID jobId);
}
