package com.prahlad.aijobportal.authservice.admin.dto.response;

import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * User projection returned by Auth Service's internal admin endpoints,
 * consumed exclusively by Admin Service via Feign. Kept separate from the
 * user-facing {@code UserResponse} record (different consumer, different
 * lifecycle) and carries a couple of admin-only fields
 * ({@code accountLocked}, {@code failedLoginAttempts}) that regular users
 * never need to see about themselves.
 */
public record AdminUserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        AccountStatus status,
        boolean emailVerified,
        boolean accountLocked,
        int failedLoginAttempts,
        Instant createdAt,
        Instant updatedAt
) {
}
