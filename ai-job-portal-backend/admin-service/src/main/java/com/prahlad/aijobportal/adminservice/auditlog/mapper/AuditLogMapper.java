package com.prahlad.aijobportal.adminservice.auditlog.mapper;

import com.prahlad.aijobportal.adminservice.auditlog.dto.response.AuditLogResponse;
import com.prahlad.aijobportal.adminservice.auditlog.entity.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);
}
