import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { JobResponse, JobSearchCriteria, JobSummaryResponse } from "@/features/jobs/types";

export interface JobListParams extends JobSearchCriteria {
  page?: number;
  size?: number;
}

/** Maps 1:1 to job-service JobController public browsing/search endpoints. */
export const jobService = {
  getAll: (params: { page?: number; size?: number }) =>
    apiClient
      .get<ApiResponse<PageResponse<JobSummaryResponse>>>("/jobs", { params })
      .then((res) => res.data),

  search: (params: JobListParams) =>
    apiClient
      .get<ApiResponse<PageResponse<JobSummaryResponse>>>("/jobs/search", { params })
      .then((res) => res.data),

  getLatest: () => apiClient.get<ApiResponse<JobSummaryResponse[]>>("/jobs/latest").then((res) => res.data),

  getFeatured: () => apiClient.get<ApiResponse<JobSummaryResponse[]>>("/jobs/featured").then((res) => res.data),

  getTrending: () => apiClient.get<ApiResponse<JobSummaryResponse[]>>("/jobs/trending").then((res) => res.data),

  getBySlug: (slug: string) => apiClient.get<ApiResponse<JobResponse>>(`/jobs/slug/${slug}`).then((res) => res.data),

  getById: (jobId: string) => apiClient.get<ApiResponse<JobResponse>>(`/jobs/${jobId}`).then((res) => res.data),

  getSimilar: (jobId: string) =>
    apiClient.get<ApiResponse<JobSummaryResponse[]>>(`/jobs/${jobId}/similar`).then((res) => res.data),
};
