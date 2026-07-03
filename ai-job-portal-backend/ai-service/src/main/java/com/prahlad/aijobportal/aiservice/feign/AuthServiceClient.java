package com.prahlad.aijobportal.aiservice.feign;

import com.prahlad.aijobportal.aiservice.feign.dto.UserSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTH-SERVICE", path = CommonConstants.API_BASE_PATH + "/auth", configuration = FeignClientConfig.class)
public interface AuthServiceClient {

    @GetMapping("/me")
    ApiResponse<UserSummaryResponse> getCurrentUser(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
