import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type {
  CandidateProfileResponse,
  CreateCandidateProfileRequest,
  ProfileCompletionResponse,
  UpdateCandidateProfileRequest,
} from "@/features/profile/types";

/** Maps 1:1 to candidate-service CandidateController (/candidate/profile). */
export const candidateService = {
  getProfile: () =>
    apiClient.get<ApiResponse<CandidateProfileResponse>>("/candidate/profile").then((res) => res.data),

  createProfile: (payload: CreateCandidateProfileRequest) =>
    apiClient
      .post<ApiResponse<CandidateProfileResponse>>("/candidate/profile", payload)
      .then((res) => res.data),

  updateProfile: (payload: UpdateCandidateProfileRequest) =>
    apiClient
      .put<ApiResponse<CandidateProfileResponse>>("/candidate/profile", payload)
      .then((res) => res.data),

  deleteProfile: () => apiClient.delete<ApiResponse<null>>("/candidate/profile").then((res) => res.data),

  getProfileCompletion: () =>
    apiClient
      .get<ApiResponse<ProfileCompletionResponse>>("/candidate/profile/completion")
      .then((res) => res.data),
};
