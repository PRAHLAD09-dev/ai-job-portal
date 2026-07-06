import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { educationService } from "@/features/profile/services/education.service";
import { extractErrorMessage } from "@/services/api-client";
import { PROFILE_COMPLETION_QUERY_KEY } from "@/features/profile/hooks/useCandidateProfile";
import type { EducationRequest } from "@/features/profile/types";

export const EDUCATION_QUERY_KEY = ["candidate", "education"] as const;

export function useEducationList() {
  return useQuery({
    queryKey: EDUCATION_QUERY_KEY,
    queryFn: () => educationService.getAll().then((res) => res.data),
  });
}

function useInvalidateEducation() {
  const queryClient = useQueryClient();
  return () => {
    queryClient.invalidateQueries({ queryKey: EDUCATION_QUERY_KEY });
    queryClient.invalidateQueries({ queryKey: PROFILE_COMPLETION_QUERY_KEY });
  };
}

export function useCreateEducation() {
  const invalidate = useInvalidateEducation();
  return useMutation({
    mutationFn: (payload: EducationRequest) => educationService.create(payload),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Education entry added");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateEducation() {
  const invalidate = useInvalidateEducation();
  return useMutation({
    mutationFn: ({ educationId, payload }: { educationId: string; payload: EducationRequest }) =>
      educationService.update(educationId, payload),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Education entry updated");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteEducation() {
  const invalidate = useInvalidateEducation();
  return useMutation({
    mutationFn: (educationId: string) => educationService.delete(educationId),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Education entry deleted");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
