import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { ExperienceRequest, ExperienceResponse } from "@/features/profile/types";

/** Maps 1:1 to candidate-service ExperienceController (/candidate/experience). */
export const experienceService = {
  getAll: () => apiClient.get<ApiResponse<ExperienceResponse[]>>("/candidate/experience").then((res) => res.data),

  create: (payload: ExperienceRequest) =>
    apiClient.post<ApiResponse<ExperienceResponse>>("/candidate/experience", payload).then((res) => res.data),

  update: (experienceId: string, payload: ExperienceRequest) =>
    apiClient
      .put<ApiResponse<ExperienceResponse>>(`/candidate/experience/${experienceId}`, payload)
      .then((res) => res.data),

  delete: (experienceId: string) =>
    apiClient.delete<ApiResponse<null>>(`/candidate/experience/${experienceId}`).then((res) => res.data),
};
