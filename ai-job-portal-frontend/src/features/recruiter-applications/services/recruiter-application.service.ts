import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type {
  ApplicationResponse,
  ApplicationSummaryResponse,
  TimelineResponse,
} from "@/features/applications/types";
import type {
  ApplicationSearchCriteria,
  ApplicationStatisticsResponse,
  RecruiterNotesRequest,
  UpdateApplicationStatusRequest,
} from "@/features/recruiter-applications/types";

export interface RecruiterApplicationListParams extends ApplicationSearchCriteria {
  page: number;
  size: number;
}

/** Maps 1:1 to application-service RecruiterApplicationController (/recruiter/applications). */
export const recruiterApplicationService = {
  getApplications: (params: RecruiterApplicationListParams) =>
    apiClient
      .get<ApiResponse<PageResponse<ApplicationSummaryResponse>>>("/recruiter/applications", { params })
      .then((res) => res.data),

  getApplicationDetail: (applicationId: string) =>
    apiClient
      .get<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}`)
      .then((res) => res.data),

  getTimeline: (applicationId: string) =>
    apiClient
      .get<ApiResponse<TimelineResponse[]>>(`/recruiter/applications/${applicationId}/timeline`)
      .then((res) => res.data),

  updateStatus: (applicationId: string, payload: UpdateApplicationStatusRequest) =>
    apiClient
      .patch<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/status`, payload)
      .then((res) => res.data),

  review: (applicationId: string) =>
    apiClient
      .post<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/review`)
      .then((res) => res.data),

  shortlist: (applicationId: string) =>
    apiClient
      .post<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/shortlist`)
      .then((res) => res.data),

  scheduleInterview: (applicationId: string, interviewDate: string) =>
    apiClient
      .post<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/interview`, null, {
        params: { interviewDate },
      })
      .then((res) => res.data),

  offer: (applicationId: string) =>
    apiClient
      .post<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/offer`)
      .then((res) => res.data),

  hire: (applicationId: string) =>
    apiClient
      .post<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/hire`)
      .then((res) => res.data),

  reject: (applicationId: string, reason?: string) =>
    apiClient
      .post<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/reject`, null, {
        params: reason ? { reason } : undefined,
      })
      .then((res) => res.data),

  addNotes: (applicationId: string, payload: RecruiterNotesRequest) =>
    apiClient
      .put<ApiResponse<ApplicationResponse>>(`/recruiter/applications/${applicationId}/notes`, payload)
      .then((res) => res.data),

  getStatistics: () =>
    apiClient
      .get<ApiResponse<ApplicationStatisticsResponse>>("/recruiter/applications/statistics")
      .then((res) => res.data),
};
