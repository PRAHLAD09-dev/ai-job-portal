package com.prahlad.aijobportal.adminservice.usermanagement.service;

import com.prahlad.aijobportal.adminservice.feign.dto.AuthUserResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.response.PageResponse;

import java.util.UUID;

/**
 * Backs Admin Service's User Management feature (DAY09_ADMIN_SERVICE.md).
 * A thin orchestration layer: every read/write is delegated to Auth
 * Service via {@code AuthServiceClient}; this service only adds
 * audit-log recording and Kafka event publishing on top — it never
 * reimplements user business logic.
 */
public interface UserManagementService {

    PageResponse<AuthUserResponse> searchUsers(String keyword, String role, String status, int page, int size);

    AuthUserResponse getUser(UUID userId);

    AuthUserResponse enableUser(UUID userId, AuthenticatedUser admin, String ipAddress);

    AuthUserResponse disableUser(UUID userId, AuthenticatedUser admin, String ipAddress);

    void deleteUser(UUID userId, AuthenticatedUser admin, String ipAddress);
}
