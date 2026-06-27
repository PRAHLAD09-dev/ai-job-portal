package com.prahlad.aijobportal.authservice.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code POST /auth/change-password}.
 * Requires the authenticated user's current password as proof of
 * possession before allowing the change.
 */
public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String newPassword
) {
}
