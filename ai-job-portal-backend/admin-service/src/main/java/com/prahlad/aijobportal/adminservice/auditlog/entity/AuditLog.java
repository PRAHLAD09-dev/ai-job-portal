package com.prahlad.aijobportal.adminservice.auditlog.entity;

import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * A single administrative event: an admin logging into the panel, or
 * performing a moderation action against a user/company/job owned by
 * another service. This is the ONLY entity Admin Service persists —
 * every other read comes from a downstream service via Feign, per
 * DAY09_ADMIN_SERVICE.md ("Admin Service owns ONLY platform
 * administration").
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_action_type", columnList = "action_type"),
        @Index(name = "idx_audit_logs_admin_id", columnList = "admin_id"),
        @Index(name = "idx_audit_logs_created_at", columnList = "created_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(name = "admin_email", nullable = false)
    private String adminEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private AuditActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 20)
    private AuditTargetType targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;
}
