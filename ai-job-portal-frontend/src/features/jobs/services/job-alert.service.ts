import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { JobAlertRequest, JobAlertResponse } from "@/features/jobs/types";

/** Maps 1:1 to job-service JobAlertController (/jobs/alerts). Every endpoint requires authentication. */
export const jobAlertService = {
  getMyAlerts: () => apiClient.get<ApiResponse<JobAlertResponse[]>>("/jobs/alerts").then((res) => res.data),

  create: (payload: JobAlertRequest) =>
    apiClient.post<ApiResponse<JobAlertResponse>>("/jobs/alerts", payload).then((res) => res.data),

  update: (alertId: string, payload: JobAlertRequest) =>
    apiClient.put<ApiResponse<JobAlertResponse>>(`/jobs/alerts/${alertId}`, payload).then((res) => res.data),

  remove: (alertId: string) =>
    apiClient.delete<ApiResponse<null>>(`/jobs/alerts/${alertId}`).then((res) => res.data),
};
