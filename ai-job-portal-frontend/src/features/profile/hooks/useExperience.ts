import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { experienceService } from "@/features/profile/services/experience.service";
import { extractErrorMessage } from "@/services/api-client";
import { PROFILE_COMPLETION_QUERY_KEY } from "@/features/profile/hooks/useCandidateProfile";
import type { ExperienceRequest } from "@/features/profile/types";

export const EXPERIENCE_QUERY_KEY = ["candidate", "experience"] as const;

export function useExperienceList() {
  return useQuery({
    queryKey: EXPERIENCE_QUERY_KEY,
    queryFn: () => experienceService.getAll().then((res) => res.data),
  });
}

function useInvalidateExperience() {
  const queryClient = useQueryClient();
  return () => {
    queryClient.invalidateQueries({ queryKey: EXPERIENCE_QUERY_KEY });
    queryClient.invalidateQueries({ queryKey: PROFILE_COMPLETION_QUERY_KEY });
  };
}

export function useCreateExperience() {
  const invalidate = useInvalidateExperience();
  return useMutation({
    mutationFn: (payload: ExperienceRequest) => experienceService.create(payload),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Experience entry added");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateExperience() {
  const invalidate = useInvalidateExperience();
  return useMutation({
    mutationFn: ({ experienceId, payload }: { experienceId: string; payload: ExperienceRequest }) =>
      experienceService.update(experienceId, payload),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Experience entry updated");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteExperience() {
  const invalidate = useInvalidateExperience();
  return useMutation({
    mutationFn: (experienceId: string) => experienceService.delete(experienceId),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Experience entry deleted");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
