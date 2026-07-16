package com.prahlad.aijobportal.recruiterservice.feign;

import com.prahlad.aijobportal.recruiterservice.feign.dto.ApplicationStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.feign.dto.ApplicationSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Synchronous Feign client to Application Service's existing
 * recruiter-facing {@code /recruiter/applications/**} endpoints, used
 * to back the DAY11 Recruiter Dashboard. Forwards the caller's own
 * already-validated bearer token (like {@link AuthServiceClient} does)
 * rather than a shared internal secret, since these endpoints already
 * resolve the requesting recruiter's company from that token. Resolved
 * via Eureka using the registered service id ({@code APPLICATION-SERVICE}).
 */
@FeignClient(name = "APPLICATION-SERVICE", path = CommonConstants.API_BASE_PATH + "/recruiter/applications", configuration = FeignClientConfig.class)
public interface ApplicationServiceClient {

    @GetMapping
    ApiResponse<PageResponse<ApplicationSummaryResponse>> getApplications(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @GetMapping("/statistics")
    ApiResponse<ApplicationStatisticsResponse> getStatistics(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
