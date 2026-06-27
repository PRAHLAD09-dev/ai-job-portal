package com.prahlad.aijobportal.authservice.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for {@code POST /auth/resend-verification}.
 */
public record ResendVerificationRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email
) {
}
