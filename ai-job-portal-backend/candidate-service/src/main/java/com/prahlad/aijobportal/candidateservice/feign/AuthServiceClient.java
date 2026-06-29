package com.prahlad.aijobportal.candidateservice.feign;

import com.prahlad.aijobportal.candidateservice.feign.dto.UserSummaryResponse;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Synchronous Feign client to Auth Service, used to retrieve the
 * authenticated user's authoritative profile (email, name, verification
 * status) when creating a Candidate profile. Resolved via Eureka using
 * the registered service id ({@code AUTH-SERVICE}), routed through the
 * load balancer rather than a hardcoded host/port.
 *
 * This does not duplicate authentication logic: it does not validate
 * credentials or issue tokens — it simply forwards the caller's own
 * already-validated bearer token to ask Auth Service "who is this user",
 * exactly the purpose {@code GET /auth/me} was built for.
 */
@FeignClient(name = "AUTH-SERVICE", path = CommonConstants.API_BASE_PATH + "/auth", configuration = FeignClientConfig.class)
public interface AuthServiceClient {

    @GetMapping("/me")
    ApiResponse<UserSummaryResponse> getCurrentUser(
            @RequestHeader(CommonConstants.AUTHORIZATION_HEADER) String bearerToken);
}
