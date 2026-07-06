import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type {
  ApplicationResponse,
  ApplicationSummaryResponse,
  CreateApplicationRequest,
  TimelineResponse,
} from "@/features/applications/types";

/** Maps 1:1 to application-service CandidateApplicationController (/applications). */
export const applicationService = {
  apply: (payload: CreateApplicationRequest) =>
    apiClient.post<ApiResponse<ApplicationResponse>>("/applications", payload).then((res) => res.data),

  withdraw: (applicationId: string) =>
    apiClient
      .post<ApiResponse<null>>(`/applications/${applicationId}/withdraw`)
      .then((res) => res.data),

  getMyApplications: (params: { page: number; size: number }) =>
    apiClient
      .get<ApiResponse<PageResponse<ApplicationSummaryResponse>>>("/applications/me", { params })
      .then((res) => res.data),

  getApplicationDetail: (applicationId: string) =>
    apiClient
      .get<ApiResponse<ApplicationResponse>>(`/applications/me/${applicationId}`)
      .then((res) => res.data),

  getTimeline: (applicationId: string) =>
    apiClient
      .get<ApiResponse<TimelineResponse[]>>(`/applications/me/${applicationId}/timeline`)
      .then((res) => res.data),
};
