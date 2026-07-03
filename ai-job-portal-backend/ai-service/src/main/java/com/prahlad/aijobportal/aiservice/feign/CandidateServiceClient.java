package com.prahlad.aijobportal.aiservice.feign;

import com.prahlad.aijobportal.aiservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Candidate Service exposes only the caller's own profile — no
 * cross-candidate lookup — so the caller's own bearer token is always
 * forwarded, which also enforces DAY07's "Candidate can analyze only
 * own resume" rule for free (there is no candidateId parameter to spoof).
 */
@FeignClient(name = "CANDIDATE-SERVICE", path = CommonConstants.API_BASE_PATH + "/candidate", configuration = FeignClientConfig.class)
public interface CandidateServiceClient {

    @GetMapping("/profile")
    ApiResponse<CandidateProfileSummaryResponse> getCurrentCandidateProfile(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
