package com.prahlad.aijobportal.authservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for {@code POST /auth/refresh-token}.
 */
public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
