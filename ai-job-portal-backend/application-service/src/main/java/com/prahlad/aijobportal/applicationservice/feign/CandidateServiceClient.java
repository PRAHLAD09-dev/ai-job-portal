package com.prahlad.aijobportal.applicationservice.feign;

import com.prahlad.aijobportal.applicationservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Synchronous Feign client to Candidate Service, used to validate the
 * applicant has a candidate profile and to resolve their display
 * name/resumes when applying for a job (DAY06's "Validate Candidate"
 * Feign client and "Resume Selection" feature). Candidate Service
 * exposes only the caller's own profile — no cross-candidate lookup —
 * so the applicant's own bearer token is always forwarded.
 */
@FeignClient(name = "CANDIDATE-SERVICE", path = CommonConstants.API_BASE_PATH + "/candidate", configuration = FeignClientConfig.class)
public interface CandidateServiceClient {

    @GetMapping("/profile")
    ApiResponse<CandidateProfileSummaryResponse> getCurrentCandidateProfile(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
