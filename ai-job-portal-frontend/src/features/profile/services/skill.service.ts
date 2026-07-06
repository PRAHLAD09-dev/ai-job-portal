import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { SkillRequest, SkillResponse } from "@/features/profile/types";

/** Maps 1:1 to candidate-service SkillController (/candidate/skills). */
export const skillService = {
  getAll: () => apiClient.get<ApiResponse<SkillResponse[]>>("/candidate/skills").then((res) => res.data),

  create: (payload: SkillRequest) =>
    apiClient.post<ApiResponse<SkillResponse>>("/candidate/skills", payload).then((res) => res.data),

  update: (skillId: string, payload: SkillRequest) =>
    apiClient.put<ApiResponse<SkillResponse>>(`/candidate/skills/${skillId}`, payload).then((res) => res.data),

  delete: (skillId: string) =>
    apiClient.delete<ApiResponse<null>>(`/candidate/skills/${skillId}`).then((res) => res.data),
};
