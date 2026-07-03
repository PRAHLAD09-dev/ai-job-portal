package com.prahlad.aijobportal.aiservice.feign;

import com.prahlad.aijobportal.aiservice.feign.dto.RecruiterSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "RECRUITER-SERVICE", path = CommonConstants.API_BASE_PATH + "/recruiter", configuration = FeignClientConfig.class)
public interface RecruiterServiceClient {

    @GetMapping("/profile")
    ApiResponse<RecruiterSummaryResponse> getCurrentRecruiter(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
