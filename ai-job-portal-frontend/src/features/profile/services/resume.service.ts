import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type { ResumeResponse } from "@/features/profile/types";

/**
 * Maps 1:1 to candidate-service ResumeController (/candidate/resumes).
 * Upload/replace are multipart/form-data with a single "file" part,
 * matching @RequestParam("file") MultipartFile on the backend exactly.
 */
export const resumeService = {
  getAll: () => apiClient.get<ApiResponse<ResumeResponse[]>>("/candidate/resumes").then((res) => res.data),

  upload: (file: File, onProgress?: (percent: number) => void) => {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient
      .post<ApiResponse<ResumeResponse>>("/candidate/resumes", formData, {
        headers: { "Content-Type": "multipart/form-data" },
        onUploadProgress: (event) => {
          if (onProgress && event.total) {
            onProgress(Math.round((event.loaded / event.total) * 100));
          }
        },
      })
      .then((res) => res.data);
  },

  replace: (resumeId: string, file: File, onProgress?: (percent: number) => void) => {
    const formData = new FormData();
    formData.append("file", file);
    return apiClient
      .put<ApiResponse<ResumeResponse>>(`/candidate/resumes/${resumeId}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
        onUploadProgress: (event) => {
          if (onProgress && event.total) {
            onProgress(Math.round((event.loaded / event.total) * 100));
          }
        },
      })
      .then((res) => res.data);
  },

  delete: (resumeId: string) =>
    apiClient.delete<ApiResponse<null>>(`/candidate/resumes/${resumeId}`).then((res) => res.data),
};
