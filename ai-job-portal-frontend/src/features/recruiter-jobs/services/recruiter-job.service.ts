import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { JobResponse, JobSavedCountResponse, JobSummaryResponse } from "@/features/jobs/types";
import type { JobFormRequest, JobStatisticsResponse } from "@/features/recruiter-jobs/types";

/** Maps 1:1 to job-service JobController recruiter management endpoints (/jobs/me/**). */
export const recruiterJobService = {
  create: (payload: JobFormRequest) =>
    apiClient.post<ApiResponse<JobResponse>>("/jobs/me", payload).then((res) => res.data),

  update: (jobId: string, payload: JobFormRequest) =>
    apiClient.put<ApiResponse<JobResponse>>(`/jobs/me/${jobId}`, payload).then((res) => res.data),

  remove: (jobId: string) => apiClient.delete<ApiResponse<null>>(`/jobs/me/${jobId}`).then((res) => res.data),

  publish: (jobId: string) =>
    apiClient.post<ApiResponse<JobResponse>>(`/jobs/me/${jobId}/publish`).then((res) => res.data),

  close: (jobId: string) =>
    apiClient.post<ApiResponse<JobResponse>>(`/jobs/me/${jobId}/close`).then((res) => res.data),

  reopen: (jobId: string) =>
    apiClient.post<ApiResponse<JobResponse>>(`/jobs/me/${jobId}/reopen`).then((res) => res.data),

  duplicate: (jobId: string) =>
    apiClient.post<ApiResponse<JobResponse>>(`/jobs/me/${jobId}/duplicate`).then((res) => res.data),

  preview: (jobId: string) =>
    apiClient.get<ApiResponse<JobResponse>>(`/jobs/me/${jobId}/preview`).then((res) => res.data),

  getMyCompanyJobs: (params: { page: number; size: number; sort?: string }) =>
    apiClient
      .get<ApiResponse<PageResponse<JobSummaryResponse>>>("/jobs/me", { params })
      .then((res) => res.data),

  getMyCompanyStatistics: () =>
    apiClient.get<ApiResponse<JobStatisticsResponse>>("/jobs/me/statistics").then((res) => res.data),

  /** DAY11 "Saved Job Statistics" — how many candidates bookmarked each of the recruiter's jobs. */
  getMyCompanySavedStatistics: () =>
    apiClient
      .get<ApiResponse<JobSavedCountResponse[]>>("/jobs/me/saved-statistics")
      .then((res) => res.data),
};
