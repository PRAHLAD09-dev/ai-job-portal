package com.prahlad.aijobportal.authservice.auth.dto.response;

import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.AuthProvider;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Response payload representing a user's public profile/identity data.
 * Never includes the password hash or any other credential material.
 */
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        AccountStatus status,
        boolean emailVerified,
        AuthProvider authProvider,
        Instant createdAt
) {
}
