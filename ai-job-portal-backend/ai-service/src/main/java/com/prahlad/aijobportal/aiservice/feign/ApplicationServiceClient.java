package com.prahlad.aijobportal.aiservice.feign;

import com.prahlad.aijobportal.aiservice.feign.dto.ApplicationSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * Application Service scopes every recruiter-side application query to
 * the caller's own company (Application Service resolves that via its
 * own Recruiter Service call), so the recruiter's own bearer token is
 * always forwarded here — enforcing DAY07's "Recruiter can generate AI
 * content only for own company" rule transitively.
 */
@FeignClient(name = "APPLICATION-SERVICE", path = CommonConstants.API_BASE_PATH + "/recruiter/applications", configuration = FeignClientConfig.class)
public interface ApplicationServiceClient {

    @GetMapping
    ApiResponse<PageResponse<ApplicationSummaryResponse>> getApplications(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken,
            @RequestParam("jobId") UUID jobId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size);
}
