import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { resumeService } from "@/features/profile/services/resume.service";
import { extractErrorMessage } from "@/services/api-client";
import { PROFILE_COMPLETION_QUERY_KEY } from "@/features/profile/hooks/useCandidateProfile";

export const RESUMES_QUERY_KEY = ["candidate", "resumes"] as const;

export function useResumesList() {
  return useQuery({
    queryKey: RESUMES_QUERY_KEY,
    queryFn: () => resumeService.getAll().then((res) => res.data),
  });
}

function useInvalidateResumes() {
  const queryClient = useQueryClient();
  return () => {
    queryClient.invalidateQueries({ queryKey: RESUMES_QUERY_KEY });
    queryClient.invalidateQueries({ queryKey: PROFILE_COMPLETION_QUERY_KEY });
  };
}

/** Tracks upload progress locally since it isn't part of TanStack Query's own state. */
export function useUploadResume() {
  const invalidate = useInvalidateResumes();
  const [progress, setProgress] = useState(0);

  const mutation = useMutation({
    mutationFn: (file: File) => resumeService.upload(file, setProgress),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Resume uploaded successfully");
      setProgress(0);
    },
    onError: (error) => {
      toast.error(extractErrorMessage(error));
      setProgress(0);
    },
  });

  return { ...mutation, progress };
}

export function useReplaceResume() {
  const invalidate = useInvalidateResumes();
  const [progress, setProgress] = useState(0);

  const mutation = useMutation({
    mutationFn: ({ resumeId, file }: { resumeId: string; file: File }) =>
      resumeService.replace(resumeId, file, setProgress),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Resume replaced successfully");
      setProgress(0);
    },
    onError: (error) => {
      toast.error(extractErrorMessage(error));
      setProgress(0);
    },
  });

  return { ...mutation, progress };
}

export function useDeleteResume() {
  const invalidate = useInvalidateResumes();
  return useMutation({
    mutationFn: (resumeId: string) => resumeService.delete(resumeId),
    onSuccess: (response) => {
      invalidate();
      toast.success(response.message || "Resume deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
