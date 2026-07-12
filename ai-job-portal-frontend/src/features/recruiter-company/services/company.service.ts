import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type {
  CompanyAssetResponse,
  CompanyLocationRequest,
  CompanyLocationResponse,
  CompanyPublicResponse,
  CompanyResponse,
  CompanySocialLinkRequest,
  CompanySocialLinkResponse,
  CompanyStatisticsResponse,
  CreateCompanyRequest,
  UpdateCompanyRequest,
} from "@/features/recruiter-company/types";

/** Maps 1:1 to recruiter-service CompanyController (/companies). */
export const companyService = {
  create: (payload: CreateCompanyRequest) =>
    apiClient.post<ApiResponse<CompanyResponse>>("/companies", payload).then((res) => res.data),

  getMine: () => apiClient.get<ApiResponse<CompanyResponse>>("/companies/me").then((res) => res.data),

  update: (payload: UpdateCompanyRequest) =>
    apiClient.put<ApiResponse<CompanyResponse>>("/companies/me", payload).then((res) => res.data),

  remove: () => apiClient.delete<ApiResponse<null>>("/companies/me").then((res) => res.data),

  getStatistics: () =>
    apiClient.get<ApiResponse<CompanyStatisticsResponse>>("/companies/me/statistics").then((res) => res.data),

  /** GET /companies/{slug}/public — no authentication required. */
  getPublicProfile: (slug: string) =>
    apiClient.get<ApiResponse<CompanyPublicResponse>>(`/companies/${slug}/public`).then((res) => res.data),
};

/** Maps 1:1 to recruiter-service CompanyLocationController (/companies/me/locations). */
export const companyLocationService = {
  getAll: () =>
    apiClient.get<ApiResponse<CompanyLocationResponse[]>>("/companies/me/locations").then((res) => res.data),

  create: (payload: CompanyLocationRequest) =>
    apiClient
      .post<ApiResponse<CompanyLocationResponse>>("/companies/me/locations", payload)
      .then((res) => res.data),

  update: (locationId: string, payload: CompanyLocationRequest) =>
    apiClient
      .put<ApiResponse<CompanyLocationResponse>>(`/companies/me/locations/${locationId}`, payload)
      .then((res) => res.data),

  remove: (locationId: string) =>
    apiClient.delete<ApiResponse<null>>(`/companies/me/locations/${locationId}`).then((res) => res.data),
};

/** Maps 1:1 to recruiter-service CompanySocialLinkController (/companies/me/social-links). */
export const companySocialLinkService = {
  getAll: () =>
    apiClient
      .get<ApiResponse<CompanySocialLinkResponse[]>>("/companies/me/social-links")
      .then((res) => res.data),

  create: (payload: CompanySocialLinkRequest) =>
    apiClient
      .post<ApiResponse<CompanySocialLinkResponse>>("/companies/me/social-links", payload)
      .then((res) => res.data),

  update: (linkId: string, payload: CompanySocialLinkRequest) =>
    apiClient
      .put<ApiResponse<CompanySocialLinkResponse>>(`/companies/me/social-links/${linkId}`, payload)
      .then((res) => res.data),

  remove: (linkId: string) =>
    apiClient.delete<ApiResponse<null>>(`/companies/me/social-links/${linkId}`).then((res) => res.data),
};

/** Maps 1:1 to recruiter-service CompanyAssetController (/companies/me/assets). Multipart uploads. */
export const companyAssetService = {
  uploadLogo: (file: File) => {
    const form = new FormData();
    form.append("file", file);
    return apiClient
      .post<ApiResponse<CompanyAssetResponse>>("/companies/me/assets/logo", form, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((res) => res.data);
  },

  replaceLogo: (file: File) => {
    const form = new FormData();
    form.append("file", file);
    return apiClient
      .put<ApiResponse<CompanyAssetResponse>>("/companies/me/assets/logo", form, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((res) => res.data);
  },

  deleteLogo: () => apiClient.delete<ApiResponse<null>>("/companies/me/assets/logo").then((res) => res.data),

  uploadBanner: (file: File) => {
    const form = new FormData();
    form.append("file", file);
    return apiClient
      .post<ApiResponse<CompanyAssetResponse>>("/companies/me/assets/banner", form, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((res) => res.data);
  },

  replaceBanner: (file: File) => {
    const form = new FormData();
    form.append("file", file);
    return apiClient
      .put<ApiResponse<CompanyAssetResponse>>("/companies/me/assets/banner", form, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((res) => res.data);
  },

  deleteBanner: () => apiClient.delete<ApiResponse<null>>("/companies/me/assets/banner").then((res) => res.data),
};
