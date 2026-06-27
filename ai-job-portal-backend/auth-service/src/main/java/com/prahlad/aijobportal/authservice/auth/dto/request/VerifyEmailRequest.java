package com.prahlad.aijobportal.authservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for {@code POST /auth/verify-email}.
 */
public record VerifyEmailRequest(

        @NotBlank(message = "Verification token is required")
        String token
) {
}
