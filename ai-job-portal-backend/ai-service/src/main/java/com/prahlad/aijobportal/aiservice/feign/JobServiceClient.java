package com.prahlad.aijobportal.aiservice.feign;

import com.prahlad.aijobportal.aiservice.feign.dto.JobDetailSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobLiteResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

/**
 * Both endpoints used here are PUBLIC on Job Service, so no bearer
 * token needs to be forwarded.
 */
@FeignClient(name = "JOB-SERVICE", path = CommonConstants.API_BASE_PATH + "/jobs", configuration = FeignClientConfig.class)
public interface JobServiceClient {

    @GetMapping("/{jobId}")
    ApiResponse<JobDetailSummaryResponse> getJobById(@PathVariable("jobId") UUID jobId);

    @GetMapping("/latest")
    ApiResponse<List<JobLiteResponse>> getLatestJobs();
}
