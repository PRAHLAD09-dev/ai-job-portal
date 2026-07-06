import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { skillService } from "@/features/profile/services/skill.service";
import { extractErrorMessage } from "@/services/api-client";
import { PROFILE_COMPLETION_QUERY_KEY } from "@/features/profile/hooks/useCandidateProfile";
import type { SkillRequest } from "@/features/profile/types";

export const SKILLS_QUERY_KEY = ["candidate", "skills"] as const;

export function useSkillsList() {
  return useQuery({
    queryKey: SKILLS_QUERY_KEY,
    queryFn: () => skillService.getAll().then((res) => res.data),
  });
}

function useInvalidateSkills() {
  const queryClient = useQueryClient();
  return () => {
    queryClient.invalidateQueries({ queryKey: SKILLS_QUERY_KEY });
    queryClient.invalidateQueries({ queryKey: PROFILE_COMPLETION_QUERY_KEY });
  };
}

export function useCreateSkill() {
  const invalidate = useInvalidateSkills();
  return useMutation({
    mutationFn: (payload: SkillRequest) => skillService.create(payload),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Skill added");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteSkill() {
  const invalidate = useInvalidateSkills();
  return useMutation({
    mutationFn: (skillId: string) => skillService.delete(skillId),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Skill removed");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
