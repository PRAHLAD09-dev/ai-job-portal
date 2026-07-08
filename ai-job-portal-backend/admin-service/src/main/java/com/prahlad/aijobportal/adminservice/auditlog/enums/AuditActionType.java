package com.prahlad.aijobportal.adminservice.auditlog.enums;

/**
 * Every action Admin Service records to its Audit Logs, per
 * DAY09_ADMIN_SERVICE.md's Audit Logs section: Login Audit, Admin
 * Actions, Company Verification Logs, and Job Moderation Logs are all
 * views over this single enum, grouped by {@link #category()}.
 */
public enum AuditActionType {
    LOGIN(AuditCategory.LOGIN),
    USER_ENABLED(AuditCategory.ADMIN_ACTION),
    USER_DISABLED(AuditCategory.ADMIN_ACTION),
    USER_DELETED(AuditCategory.ADMIN_ACTION),
    COMPANY_VERIFIED(AuditCategory.COMPANY_VERIFICATION),
    COMPANY_REJECTED(AuditCategory.COMPANY_VERIFICATION),
    COMPANY_SUSPENDED(AuditCategory.COMPANY_VERIFICATION),
    JOB_REMOVED(AuditCategory.JOB_MODERATION),
    JOB_RESTORED(AuditCategory.JOB_MODERATION),
    JOB_FEATURED(AuditCategory.JOB_MODERATION),
    JOB_UNFEATURED(AuditCategory.JOB_MODERATION);

    private final AuditCategory category;

    AuditActionType(AuditCategory category) {
        this.category = category;
    }

    public AuditCategory category() {
        return category;
    }

    public enum AuditCategory {
        LOGIN,
        ADMIN_ACTION,
        COMPANY_VERIFICATION,
        JOB_MODERATION
    }
}
