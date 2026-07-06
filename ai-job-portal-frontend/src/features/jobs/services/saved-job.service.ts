import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type { SavedJobResponse } from "@/features/jobs/types";

/** Maps 1:1 to job-service SavedJobController (/jobs/saved). */
export const savedJobService = {
  getMySavedJobs: (params: { page?: number; size?: number }) =>
    apiClient
      .get<ApiResponse<PageResponse<SavedJobResponse>>>("/jobs/saved", { params })
      .then((res) => res.data),

  save: (jobId: string) =>
    apiClient.post<ApiResponse<SavedJobResponse>>(`/jobs/saved/${jobId}`).then((res) => res.data),

  unsave: (jobId: string) =>
    apiClient.delete<ApiResponse<null>>(`/jobs/saved/${jobId}`).then((res) => res.data),
};
