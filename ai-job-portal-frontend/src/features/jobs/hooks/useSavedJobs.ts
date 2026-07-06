import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { savedJobService } from "@/features/jobs/services/saved-job.service";
import { extractErrorMessage } from "@/services/api-client";

export const SAVED_JOBS_QUERY_KEY = ["jobs", "saved"] as const;

export function useSavedJobsList(params: { page?: number; size?: number }) {
  return useQuery({
    queryKey: [...SAVED_JOBS_QUERY_KEY, params],
    queryFn: () => savedJobService.getMySavedJobs(params).then((res) => res.data),
  });
}

export function useSaveJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => savedJobService.save(jobId),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: SAVED_JOBS_QUERY_KEY });
      toast.success(response.message || "Job saved");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUnsaveJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => savedJobService.unsave(jobId),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: SAVED_JOBS_QUERY_KEY });
      toast.success(response.message || "Job removed from saved jobs");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

/**
 * JobSummaryResponse has no `isSaved` field, so bookmark state across
 * job cards is derived client-side from the candidate's saved-jobs list.
 * A page size of 100 comfortably covers realistic saved-job counts
 * without needing a dedicated backend "is this job saved" endpoint.
 */
export function useSavedJobIds() {
  const { data } = useSavedJobsList({ page: 0, size: 100 });
  return new Set((data?.content ?? []).map((entry) => entry.job.id));
}
