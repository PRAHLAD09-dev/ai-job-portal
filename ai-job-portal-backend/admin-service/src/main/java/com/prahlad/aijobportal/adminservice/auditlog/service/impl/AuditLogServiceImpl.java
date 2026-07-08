package com.prahlad.aijobportal.adminservice.auditlog.service.impl;

import com.prahlad.aijobportal.adminservice.auditlog.dto.response.AuditLogResponse;
import com.prahlad.aijobportal.adminservice.auditlog.entity.AuditLog;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.mapper.AuditLogMapper;
import com.prahlad.aijobportal.adminservice.auditlog.repository.AuditLogRepository;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.auditlog.specification.AuditLogSpecification;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public void record(AuthenticatedUser admin, AuditActionType actionType, AuditTargetType targetType,
                        UUID targetId, String description, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .adminId(admin.userId())
                .adminEmail(admin.email())
                .actionType(actionType)
                .targetType(targetType)
                .targetId(targetId)
                .description(description)
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log recorded: admin={} action={} target={}:{}", admin.email(), actionType, targetType, targetId);
    }

    @Override
    public Page<AuditLogResponse> getLoginAudit(UUID adminId, Pageable pageable) {
        return auditLogRepository.findAll(
                        AuditLogSpecification.byCategory(AuditActionType.AuditCategory.LOGIN, adminId), pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getAdminActions(UUID adminId, Pageable pageable) {
        return auditLogRepository.findAll(
                        AuditLogSpecification.byCategory(AuditActionType.AuditCategory.ADMIN_ACTION, adminId), pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getCompanyVerificationLogs(UUID adminId, Pageable pageable) {
        return auditLogRepository.findAll(
                        AuditLogSpecification.byCategory(AuditActionType.AuditCategory.COMPANY_VERIFICATION, adminId), pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getJobModerationLogs(UUID adminId, Pageable pageable) {
        return auditLogRepository.findAll(
                        AuditLogSpecification.byCategory(AuditActionType.AuditCategory.JOB_MODERATION, adminId), pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    public Page<AuditLogResponse> getRecentActivity(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(auditLogMapper::toResponse);
    }
}
