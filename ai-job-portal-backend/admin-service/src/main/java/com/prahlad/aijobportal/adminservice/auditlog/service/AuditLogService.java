package com.prahlad.aijobportal.adminservice.auditlog.service;

import com.prahlad.aijobportal.adminservice.auditlog.dto.response.AuditLogResponse;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Single source of truth for writing and reading Admin Service's own
 * Audit Logs (DAY09_ADMIN_SERVICE.md's Audit Logs section). Every other
 * admin feature module (User/Company/Job Management) calls
 * {@link #record} rather than writing to {@code AuditLogRepository}
 * directly, so audit-entry shape/behavior never needs to be duplicated.
 */
public interface AuditLogService {

    void record(AuthenticatedUser admin, AuditActionType actionType, AuditTargetType targetType,
                UUID targetId, String description, String ipAddress);

    Page<AuditLogResponse> getLoginAudit(UUID adminId, Pageable pageable);

    Page<AuditLogResponse> getAdminActions(UUID adminId, Pageable pageable);

    Page<AuditLogResponse> getCompanyVerificationLogs(UUID adminId, Pageable pageable);

    Page<AuditLogResponse> getJobModerationLogs(UUID adminId, Pageable pageable);

    Page<AuditLogResponse> getRecentActivity(Pageable pageable);
}
