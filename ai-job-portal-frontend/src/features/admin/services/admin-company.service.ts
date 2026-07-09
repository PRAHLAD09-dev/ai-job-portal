import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { AdminCompanyResponse } from "@/features/admin/types";

export interface AdminCompanySearchParams {
  keyword?: string;
  status?: string;
  page: number;
  size: number;
}

/** Maps 1:1 to admin-service CompanyManagementController (/admin/companies). */
export const adminCompanyService = {
  search: (params: AdminCompanySearchParams) =>
    apiClient
      .get<ApiResponse<PageResponse<AdminCompanyResponse>>>("/admin/companies", { params })
      .then((res) => res.data),

  getById: (companyId: string) =>
    apiClient.get<ApiResponse<AdminCompanyResponse>>(`/admin/companies/${companyId}`).then((res) => res.data),

  verify: (companyId: string) =>
    apiClient
      .patch<ApiResponse<AdminCompanyResponse>>(`/admin/companies/${companyId}/verify`)
      .then((res) => res.data),

  reject: (companyId: string) =>
    apiClient
      .patch<ApiResponse<AdminCompanyResponse>>(`/admin/companies/${companyId}/reject`)
      .then((res) => res.data),

  suspend: (companyId: string) =>
    apiClient
      .patch<ApiResponse<AdminCompanyResponse>>(`/admin/companies/${companyId}/suspend`)
      .then((res) => res.data),
};
