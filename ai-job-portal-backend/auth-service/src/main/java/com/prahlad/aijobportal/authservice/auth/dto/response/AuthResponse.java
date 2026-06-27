package com.prahlad.aijobportal.authservice.auth.dto.response;

/**
 * Response payload returned on successful login or token refresh.
 * Carries the bearer access token plus the rotating refresh token.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserResponse user
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresInSeconds, UserResponse user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, user);
    }
}
