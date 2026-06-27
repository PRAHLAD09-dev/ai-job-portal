package com.prahlad.aijobportal.authservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code POST /auth/reset-password}.
 */
public record ResetPasswordRequest(

        @NotBlank(message = "Reset token is required")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String newPassword
) {
}
