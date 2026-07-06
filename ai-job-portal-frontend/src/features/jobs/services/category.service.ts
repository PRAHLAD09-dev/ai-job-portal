import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { JobCategoryResponse } from "@/features/jobs/types";

/** Maps 1:1 to job-service JobCategoryController (/job-categories). */
export const categoryService = {
  getAll: () => apiClient.get<ApiResponse<JobCategoryResponse[]>>("/job-categories").then((res) => res.data),

  getPopularSkills: () =>
    apiClient.get<ApiResponse<string[]>>("/job-categories/popular-skills").then((res) => res.data),
};
