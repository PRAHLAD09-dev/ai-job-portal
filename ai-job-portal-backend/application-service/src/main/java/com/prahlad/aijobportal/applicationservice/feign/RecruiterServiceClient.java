package com.prahlad.aijobportal.applicationservice.feign;

import com.prahlad.aijobportal.applicationservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Synchronous Feign client to Recruiter Service, used to resolve the
 * authenticated recruiter's {@code companyId} so recruiter-side
 * endpoints can be scoped to "only company applications" per
 * DAY06_APPLICATION_SERVICE.md's Security section. Resolved via Eureka
 * using the registered service id ({@code RECRUITER-SERVICE}). Forwards
 * the caller's own already-validated bearer token.
 */
@FeignClient(name = "RECRUITER-SERVICE", path = CommonConstants.API_BASE_PATH + "/recruiter", configuration = FeignClientConfig.class)
public interface RecruiterServiceClient {

    @GetMapping("/profile")
    ApiResponse<RecruiterSummaryResponse> getCurrentRecruiter(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
