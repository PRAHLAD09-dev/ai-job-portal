import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { recruiterService } from "@/features/recruiter-profile/services/recruiter.service";
import { extractErrorMessage } from "@/services/api-client";
import type { UpdateRecruiterProfileRequest } from "@/features/recruiter-profile/types";

export const RECRUITER_PROFILE_QUERY_KEY = ["recruiter", "profile"] as const;

export function useRecruiterProfile() {
  return useQuery({
    queryKey: RECRUITER_PROFILE_QUERY_KEY,
    queryFn: () => recruiterService.getMyProfile().then((res) => res.data),
    staleTime: 30_000,
  });
}

export function useUpdateRecruiterProfile() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: UpdateRecruiterProfileRequest) => recruiterService.updateMyProfile(payload),
    onSuccess: (response) => {
      queryClient.setQueryData(RECRUITER_PROFILE_QUERY_KEY, response.data);
      toast.success(response.message || "Profile updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
