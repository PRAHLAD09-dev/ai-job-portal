import { useMutation, useQuery } from "@tanstack/react-query";
import {
  adminAuditLogService,
  adminDashboardService,
  type AuditLogParams,
} from "@/features/admin/services/admin-dashboard.service";

export function useAdminDashboard() {
  return useQuery({
    queryKey: ["admin", "dashboard"],
    queryFn: () => adminDashboardService.get().then((res) => res.data),
    staleTime: 30_000,
  });
}

/** Fire-and-forget — records that the current admin session opened the admin panel. */
export function useRecordAdminLogin() {
  return useMutation({
    mutationFn: () => adminAuditLogService.recordLogin(),
  });
}

export function useLoginAudit(params: AuditLogParams) {
  return useQuery({
    queryKey: ["admin", "audit-logs", "login", params],
    queryFn: () => adminAuditLogService.getLoginAudit(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

export function useAdminActionsAudit(params: AuditLogParams) {
  return useQuery({
    queryKey: ["admin", "audit-logs", "admin-actions", params],
    queryFn: () => adminAuditLogService.getAdminActions(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

export function useCompanyVerificationAudit(params: AuditLogParams) {
  return useQuery({
    queryKey: ["admin", "audit-logs", "company-verification", params],
    queryFn: () => adminAuditLogService.getCompanyVerificationLogs(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

export function useJobModerationAudit(params: AuditLogParams) {
  return useQuery({
    queryKey: ["admin", "audit-logs", "job-moderation", params],
    queryFn: () => adminAuditLogService.getJobModerationLogs(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}
