package com.prahlad.aijobportal.adminservice.usermanagement.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.event.AdminEventPublisher;
import com.prahlad.aijobportal.adminservice.event.dto.UserDisabledEvent;
import com.prahlad.aijobportal.adminservice.feign.AuthServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.AuthUserResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.adminservice.usermanagement.service.UserManagementService;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Thin orchestration layer over {@link AuthServiceClient}. Never
 * reimplements Auth Service's user business logic — every read/write
 * happens on that service; this class only adds audit-log recording and
 * Kafka event publishing (per DAY09_ADMIN_SERVICE.md's Kafka section).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final AuthServiceClient authServiceClient;
    private final AuditLogService auditLogService;
    private final AdminEventPublisher adminEventPublisher;

    @Override
    @CircuitBreaker(name = "authService")
    @Retry(name = "authService")
    public PageResponse<AuthUserResponse> searchUsers(String keyword, String role, String status, int page, int size) {
        return authServiceClient.searchUsers(keyword, role, status, page, size).getData();
    }

    @Override
    @CircuitBreaker(name = "authService")
    @Retry(name = "authService")
    public AuthUserResponse getUser(UUID userId) {
        return authServiceClient.getUser(userId).getData();
    }

    @Override
    @CircuitBreaker(name = "authService")
    public AuthUserResponse enableUser(UUID userId, AuthenticatedUser admin, String ipAddress) {
        AuthUserResponse user = authServiceClient.enableUser(userId).getData();

        auditLogService.record(admin, AuditActionType.USER_ENABLED, AuditTargetType.USER, userId,
                "Enabled user " + user.email(), ipAddress);

        return user;
    }

    @Override
    @CircuitBreaker(name = "authService")
    public AuthUserResponse disableUser(UUID userId, AuthenticatedUser admin, String ipAddress) {
        AuthUserResponse user = authServiceClient.disableUser(userId).getData();

        auditLogService.record(admin, AuditActionType.USER_DISABLED, AuditTargetType.USER, userId,
                "Disabled user " + user.email(), ipAddress);

        adminEventPublisher.publishUserDisabled(
                new UserDisabledEvent(userId, user.email(), admin.userId(), Instant.now()));

        return user;
    }

    @Override
    @CircuitBreaker(name = "authService")
    public void deleteUser(UUID userId, AuthenticatedUser admin, String ipAddress) {
        AuthUserResponse user = authServiceClient.getUser(userId).getData();
        authServiceClient.deleteUser(userId);

        auditLogService.record(admin, AuditActionType.USER_DELETED, AuditTargetType.USER, userId,
                "Deleted user " + user.email(), ipAddress);
    }
}
