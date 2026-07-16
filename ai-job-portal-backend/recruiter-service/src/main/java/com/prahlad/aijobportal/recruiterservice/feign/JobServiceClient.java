package com.prahlad.aijobportal.recruiterservice.feign;

import com.prahlad.aijobportal.recruiterservice.feign.dto.JobSavedCountResponse;
import com.prahlad.aijobportal.recruiterservice.feign.dto.JobStatisticsResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Synchronous Feign client to Job Service's existing recruiter-facing
 * {@code /jobs/me/**} endpoints, used to back the DAY11 Recruiter
 * Dashboard. Forwards the caller's own already-validated bearer token
 * (like {@link AuthServiceClient} does) rather than a shared internal
 * secret, since these endpoints already resolve the requesting
 * recruiter's company from that token — no new internal-only surface
 * needed for job/saved-job statistics. Resolved via Eureka using the
 * registered service id ({@code JOB-SERVICE}).
 */
@FeignClient(name = "JOB-SERVICE", path = CommonConstants.API_BASE_PATH + "/jobs/me", configuration = FeignClientConfig.class)
public interface JobServiceClient {

    @GetMapping("/statistics")
    ApiResponse<JobStatisticsResponse> getMyCompanyStatistics(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);

    @GetMapping("/saved-statistics")
    ApiResponse<List<JobSavedCountResponse>> getMyCompanySavedJobStatistics(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
