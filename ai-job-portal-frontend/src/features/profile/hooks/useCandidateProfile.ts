import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { candidateService } from "@/features/profile/services/candidate.service";
import { extractErrorMessage } from "@/services/api-client";
import type { CreateCandidateProfileRequest, UpdateCandidateProfileRequest } from "@/features/profile/types";

export const PROFILE_QUERY_KEY = ["candidate", "profile"] as const;
export const PROFILE_COMPLETION_QUERY_KEY = ["candidate", "profile", "completion"] as const;

/**
 * Fetches the authenticated candidate profile. A 404 means the profile
 * has not been created yet (first login) — callers should render the
 * "create your profile" state rather than an error, so 404 is treated
 * as a normal, non-error result (`profile: null`).
 */
export function useCandidateProfile() {
  return useQuery({
    queryKey: PROFILE_QUERY_KEY,
    queryFn: async () => {
      try {
        const response = await candidateService.getProfile();
        return response.data;
      } catch (error: unknown) {
        const status = (error as { response?: { status?: number } })?.response?.status;
        if (status === 404) return null;
        throw error;
      }
    },
    staleTime: 30_000,
  });
}

export function useProfileCompletion() {
  return useQuery({
    queryKey: PROFILE_COMPLETION_QUERY_KEY,
    queryFn: () => candidateService.getProfileCompletion().then((res) => res.data),
  });
}

export function useCreateProfile() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateCandidateProfileRequest) => candidateService.createProfile(payload),
    onSuccess: (response) => {
      queryClient.setQueryData(PROFILE_QUERY_KEY, response.data);
      queryClient.invalidateQueries({ queryKey: PROFILE_COMPLETION_QUERY_KEY });
      toast.success(response.message || "Profile created successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateProfile() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: UpdateCandidateProfileRequest) => candidateService.updateProfile(payload),
    onSuccess: (response) => {
      queryClient.setQueryData(PROFILE_QUERY_KEY, response.data);
      queryClient.invalidateQueries({ queryKey: PROFILE_COMPLETION_QUERY_KEY });
      toast.success(response.message || "Profile updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
