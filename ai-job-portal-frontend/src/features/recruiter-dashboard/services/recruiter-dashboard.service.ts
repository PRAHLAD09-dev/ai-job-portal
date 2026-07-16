import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { RecruiterDashboardResponse } from "@/features/recruiter-dashboard/types";

/** Maps 1:1 to recruiter-service's RecruiterDashboardController. */
export const recruiterDashboardService = {
  getDashboard: () =>
    apiClient.get<ApiResponse<RecruiterDashboardResponse>>("/recruiter/dashboard").then((res) => res.data),
};
