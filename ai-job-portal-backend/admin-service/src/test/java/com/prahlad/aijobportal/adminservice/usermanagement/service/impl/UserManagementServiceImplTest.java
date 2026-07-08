package com.prahlad.aijobportal.adminservice.usermanagement.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.event.AdminEventPublisher;
import com.prahlad.aijobportal.adminservice.event.dto.UserDisabledEvent;
import com.prahlad.aijobportal.adminservice.feign.AuthServiceClient;
import com.prahlad.aijobportal.adminservice.feign.dto.AuthUserResponse;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

    @Mock private AuthServiceClient authServiceClient;
    @Mock private AuditLogService auditLogService;
    @Mock private AdminEventPublisher adminEventPublisher;

    private UserManagementServiceImpl userManagementService;

    private AuthenticatedUser admin;
    private UUID userId;
    private AuthUserResponse disabledUser;

    @BeforeEach
    void setUp() {
        userManagementService = new UserManagementServiceImpl(authServiceClient, auditLogService, adminEventPublisher);

        admin = new AuthenticatedUser(UUID.randomUUID(), "admin@example.com", Set.of("ADMIN"));
        userId = UUID.randomUUID();
        disabledUser = new AuthUserResponse(userId, "user@example.com", "John", "Doe",
                Set.of("CANDIDATE"), "DISABLED", true, false, 0, Instant.now(), Instant.now());
    }

    @Test
    void disableUser_recordsAuditLogAndPublishesEvent() {
        when(authServiceClient.disableUser(userId)).thenReturn(ApiResponse.success(disabledUser));

        AuthUserResponse result = userManagementService.disableUser(userId, admin, "127.0.0.1");

        assertThat(result).isEqualTo(disabledUser);
        verify(auditLogService).record(eq(admin), eq(AuditActionType.USER_DISABLED),
                eq(AuditTargetType.USER), eq(userId), any(String.class), eq("127.0.0.1"));
        verify(adminEventPublisher).publishUserDisabled(any(UserDisabledEvent.class));
    }

    @Test
    void deleteUser_fetchesUserThenDeletesAndRecordsAudit() {
        when(authServiceClient.getUser(userId)).thenReturn(ApiResponse.success(disabledUser));
        when(authServiceClient.deleteUser(userId)).thenReturn(ApiResponse.success(null));

        userManagementService.deleteUser(userId, admin, "127.0.0.1");

        verify(authServiceClient).deleteUser(userId);
        verify(auditLogService).record(eq(admin), eq(AuditActionType.USER_DELETED),
                eq(AuditTargetType.USER), eq(userId), any(String.class), eq("127.0.0.1"));
    }
}
