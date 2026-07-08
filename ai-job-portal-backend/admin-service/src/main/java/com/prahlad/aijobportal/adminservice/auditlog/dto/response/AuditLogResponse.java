package com.prahlad.aijobportal.adminservice.auditlog.dto.response;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID adminId,
        String adminEmail,
        AuditActionType actionType,
        AuditTargetType targetType,
        UUID targetId,
        String description,
        String ipAddress,
        Instant createdAt
) {
}
