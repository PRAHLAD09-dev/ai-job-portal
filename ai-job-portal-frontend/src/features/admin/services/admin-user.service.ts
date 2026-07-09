import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { AdminUserResponse } from "@/features/admin/types";

export interface AdminUserSearchParams {
  keyword?: string;
  role?: string;
  status?: string;
  page: number;
  size: number;
}

/** Maps 1:1 to admin-service UserManagementController (/admin/users). */
export const adminUserService = {
  search: (params: AdminUserSearchParams) =>
    apiClient.get<ApiResponse<PageResponse<AdminUserResponse>>>("/admin/users", { params }).then((res) => res.data),

  getById: (userId: string) =>
    apiClient.get<ApiResponse<AdminUserResponse>>(`/admin/users/${userId}`).then((res) => res.data),

  enable: (userId: string) =>
    apiClient.patch<ApiResponse<AdminUserResponse>>(`/admin/users/${userId}/enable`).then((res) => res.data),

  disable: (userId: string) =>
    apiClient.patch<ApiResponse<AdminUserResponse>>(`/admin/users/${userId}/disable`).then((res) => res.data),

  /** SUPER_ADMIN only — enforced server-side too. */
  remove: (userId: string) =>
    apiClient.delete<ApiResponse<null>>(`/admin/users/${userId}`).then((res) => res.data),
};
