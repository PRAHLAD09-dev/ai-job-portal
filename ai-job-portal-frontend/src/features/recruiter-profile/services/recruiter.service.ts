import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { RecruiterResponse, UpdateRecruiterProfileRequest } from "@/features/recruiter-profile/types";

/** Maps 1:1 to recruiter-service RecruiterController (/recruiter/profile). */
export const recruiterService = {
  getMyProfile: () =>
    apiClient.get<ApiResponse<RecruiterResponse>>("/recruiter/profile").then((res) => res.data),

  updateMyProfile: (payload: UpdateRecruiterProfileRequest) =>
    apiClient.put<ApiResponse<RecruiterResponse>>("/recruiter/profile", payload).then((res) => res.data),
};
