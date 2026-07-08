package com.prahlad.aijobportal.adminservice.auditlog.controller;

import com.prahlad.aijobportal.adminservice.auditlog.dto.response.AuditLogResponse;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditTargetType;
import com.prahlad.aijobportal.adminservice.auditlog.service.AuditLogService;
import com.prahlad.aijobportal.adminservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import com.prahlad.aijobportal.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Admin Service's Audit Logs feature (DAY09_ADMIN_SERVICE.md): Login
 * Audit, Admin Actions, Company Verification Logs, and Job Moderation
 * Logs are all filtered views over the single {@code audit_logs} table,
 * which this service exclusively owns — every entry here is written by
 * {@code AuditLogService.record(...)} from within this same service
 * (User/Company/Job Management), never by another microservice.
 *
 * "Login Audit" specifically tracks logins to the ADMIN PANEL (i.e. an
 * ADMIN/SUPER_ADMIN authenticating and then confirming that fact to this
 * service via {@code POST /login}), not every platform user's login —
 * platform-wide login events are owned and would need to be audited by
 * Auth Service itself; duplicating that here would violate the "never
 * duplicate business logic" rule. See README.md's Admin Service section
 * for the full rationale.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/admin/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Admin - Audit Logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping("/login")
    @Operation(summary = "Record that the currently authenticated admin has logged into the admin panel")
    public ResponseEntity<ApiResponse<Void>> recordLogin(@AuthenticationPrincipal AuthenticatedUser admin,
                                                           HttpServletRequest request) {
        auditLogService.record(admin, AuditActionType.LOGIN, AuditTargetType.ADMIN_SESSION, admin.userId(),
                "Admin " + admin.email() + " logged into the admin panel", request.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Login recorded", null));
    }

    @GetMapping("/login")
    @Operation(summary = "View login audit trail")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getLoginAudit(
            @RequestParam(required = false) UUID adminId,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<AuditLogResponse> page = auditLogService.getLoginAudit(adminId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/admin-actions")
    @Operation(summary = "View admin actions audit trail (user enable/disable/delete)")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAdminActions(
            @RequestParam(required = false) UUID adminId,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<AuditLogResponse> page = auditLogService.getAdminActions(adminId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/company-verification")
    @Operation(summary = "View company verification audit trail")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getCompanyVerificationLogs(
            @RequestParam(required = false) UUID adminId,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<AuditLogResponse> page = auditLogService.getCompanyVerificationLogs(adminId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/job-moderation")
    @Operation(summary = "View job moderation audit trail")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getJobModerationLogs(
            @RequestParam(required = false) UUID adminId,
            @PageableDefault(size = CommonConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<AuditLogResponse> page = auditLogService.getJobModerationLogs(adminId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }
}
