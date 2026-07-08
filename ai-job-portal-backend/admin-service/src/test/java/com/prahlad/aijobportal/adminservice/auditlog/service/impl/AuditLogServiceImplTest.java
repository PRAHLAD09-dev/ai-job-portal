package com.prahlad.aijobportal.adminservice.auditlog.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.dto.response.AuditLogResponse;
import com.prahlad.aijobportal.adminservice.auditlog.entity.AuditLog;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.mapper.AuditLogMapper;
import com.prahlad.aijobportal.adminservice.auditlog.repository.AuditLogRepository;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock private AuditLogRepository auditLogRepository;
    @Mock private AuditLogMapper auditLogMapper;

    private AuditLogServiceImpl auditLogService;

    private AuthenticatedUser admin;

    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogServiceImpl(auditLogRepository, auditLogMapper);
        admin = new AuthenticatedUser(UUID.randomUUID(), "admin@example.com", Set.of("ADMIN"));
    }

    @Test
    void record_savesAuditLogWithCorrectFields() {
        UUID targetId = UUID.randomUUID();
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(AuditLog.builder().build());

        auditLogService.record(admin, AuditActionType.USER_DISABLED, AuditTargetType.USER,
                targetId, "Disabled user test@example.com", "127.0.0.1");

        verify(auditLogRepository).save(captor.capture());
        AuditLog saved = captor.getValue();
        assertThat(saved.getAdminId()).isEqualTo(admin.userId());
        assertThat(saved.getAdminEmail()).isEqualTo(admin.email());
        assertThat(saved.getActionType()).isEqualTo(AuditActionType.USER_DISABLED);
        assertThat(saved.getTargetType()).isEqualTo(AuditTargetType.USER);
        assertThat(saved.getTargetId()).isEqualTo(targetId);
        assertThat(saved.getIpAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    void getJobModerationLogs_delegatesToRepositoryWithCategorySpecification() {
        Pageable pageable = Pageable.ofSize(10);
        Page<AuditLog> emptyPage = new PageImpl<>(java.util.List.of());
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        Page<AuditLogResponse> result = auditLogService.getJobModerationLogs(null, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(auditLogRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
