package com.prahlad.aijobportal.authservice.auth.dto.request;

import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for {@code POST /auth/oauth/google}. {@code role} is
 * only used the first time this Google account signs in (it decides
 * whether a brand-new account is created as CANDIDATE or RECRUITER); it
 * is ignored on every later login, where the account's existing role
 * always wins.
 */
public record GoogleAuthRequest(

        @NotBlank(message = "Google ID token is required")
        String idToken,

        @NotNull(message = "Role is required")
        RoleName role
) {
}
