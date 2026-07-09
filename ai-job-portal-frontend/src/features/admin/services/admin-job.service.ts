import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { AdminJobResponse } from "@/features/admin/types";

export interface AdminJobSearchParams {
  keyword?: string;
  status?: string;
  companyId?: string;
  page: number;
  size: number;
}

/** Maps 1:1 to admin-service JobManagementController (/admin/jobs). */
export const adminJobService = {
  search: (params: AdminJobSearchParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminJobResponse>>>("/admin/jobs", { params }).then((res) => res.data),

  getById: (jobId: string) =>
    apiClient.get<ApiResponse<AdminJobResponse>>(`/admin/jobs/${jobId}`).then((res) => res.data),

  remove: (jobId: string) =>
    apiClient.patch<ApiResponse<AdminJobResponse>>(`/admin/jobs/${jobId}/remove`).then((res) => res.data),

  restore: (jobId: string) =>
    apiClient.patch<ApiResponse<AdminJobResponse>>(`/admin/jobs/${jobId}/restore`).then((res) => res.data),

  feature: (jobId: string) =>
    apiClient.patch<ApiResponse<AdminJobResponse>>(`/admin/jobs/${jobId}/feature`).then((res) => res.data),

  unfeature: (jobId: string) =>
    apiClient.patch<ApiResponse<AdminJobResponse>>(`/admin/jobs/${jobId}/unfeature`).then((res) => res.data),
};
