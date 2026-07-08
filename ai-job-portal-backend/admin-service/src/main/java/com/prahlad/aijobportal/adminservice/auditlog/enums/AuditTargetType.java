package com.prahlad.aijobportal.adminservice.auditlog.enums;

/**
 * The kind of entity an {@code AuditLog} entry's {@code targetId} points
 * at. The target itself is never owned by Admin Service (it always
 * lives in Auth/Recruiter/Job Service) — this is purely a reference for
 * display/filtering.
 */
public enum AuditTargetType {
    USER,
    COMPANY,
    JOB,
    ADMIN_SESSION
}
