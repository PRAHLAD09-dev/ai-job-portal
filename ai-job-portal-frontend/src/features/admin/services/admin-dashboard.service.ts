import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { AuditLogResponse, DashboardChartsResponse, DashboardResponse } from "@/features/admin/types";

/** Maps 1:1 to admin-service DashboardController (/admin/dashboard). */
export const adminDashboardService = {
  get: () => apiClient.get<ApiResponse<DashboardResponse>>("/admin/dashboard").then((res) => res.data),

  getCharts: (userGrowthDays = 30) =>
    apiClient
      .get<ApiResponse<DashboardChartsResponse>>("/admin/dashboard/charts", { params: { userGrowthDays } })
      .then((res) => res.data),
};

export interface AuditLogParams {
  adminId?: string;
  page: number;
  size: number;
}

/** Maps 1:1 to admin-service AuditLogController (/admin/audit-logs). */
export const adminAuditLogService = {
  recordLogin: () => apiClient.post<ApiResponse<null>>("/admin/audit-logs/login").then((res) => res.data),

  getLoginAudit: (params: AuditLogParams) =>
    apiClient
      .get<ApiResponse<PageResponse<AuditLogResponse>>>("/admin/audit-logs/login", { params })
      .then((res) => res.data),

  getAdminActions: (params: AuditLogParams) =>
    apiClient
      .get<ApiResponse<PageResponse<AuditLogResponse>>>("/admin/audit-logs/admin-actions", { params })
      .then((res) => res.data),

  getCompanyVerificationLogs: (params: AuditLogParams) =>
    apiClient
      .get<ApiResponse<PageResponse<AuditLogResponse>>>("/admin/audit-logs/company-verification", { params })
      .then((res) => res.data),

  getJobModerationLogs: (params: AuditLogParams) =>
    apiClient
      .get<ApiResponse<PageResponse<AuditLogResponse>>>("/admin/audit-logs/job-moderation", { params })
      .then((res) => res.data),
};
