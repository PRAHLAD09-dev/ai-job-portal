import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { EducationRequest, EducationResponse } from "@/features/profile/types";

/** Maps 1:1 to candidate-service EducationController (/candidate/education). */
export const educationService = {
  getAll: () => apiClient.get<ApiResponse<EducationResponse[]>>("/candidate/education").then((res) => res.data),

  create: (payload: EducationRequest) =>
    apiClient.post<ApiResponse<EducationResponse>>("/candidate/education", payload).then((res) => res.data),

  update: (educationId: string, payload: EducationRequest) =>
    apiClient
      .put<ApiResponse<EducationResponse>>(`/candidate/education/${educationId}`, payload)
      .then((res) => res.data),

  delete: (educationId: string) =>
    apiClient.delete<ApiResponse<null>>(`/candidate/education/${educationId}`).then((res) => res.data),
};
