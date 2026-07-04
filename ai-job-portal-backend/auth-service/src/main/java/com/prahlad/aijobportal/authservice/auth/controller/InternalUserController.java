package com.prahlad.aijobportal.authservice.auth.controller;

import com.prahlad.aijobportal.authservice.auth.dto.response.UserResponse;
import com.prahlad.aijobportal.authservice.auth.service.AuthService;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal-only, service-to-service endpoint. Never routed through the
 * API Gateway and never callable with a normal user bearer token —
 * authenticated exclusively by {@code InternalServiceAuthFilter} via a
 * shared secret header, per DECISIONS.md ("Internal JWT for
 * Service-to-Service Communication").
 *
 * Added to support the Notification Service, whose Kafka listeners have
 * no incoming HTTP request/bearer token and therefore cannot call
 * {@code /auth/me}. Reuses the existing {@code AuthService.getCurrentUser}
 * method verbatim; no business logic changes to the Auth Service.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/auth/internal")
@RequiredArgsConstructor
@Tag(name = "Internal", description = "Service-to-service endpoints, not exposed through the API Gateway")
public class InternalUserController {

    private final AuthService authService;

    @GetMapping("/users/{userId}")
    @Operation(summary = "Resolve a user's e-mail/name by id (internal service callers only)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID userId) {
        UserResponse response = authService.getCurrentUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
