import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { recruiterJobService } from "@/features/recruiter-jobs/services/recruiter-job.service";
import { extractErrorMessage } from "@/services/api-client";
import type { JobFormRequest } from "@/features/recruiter-jobs/types";

export const RECRUITER_JOBS_QUERY_KEY = ["recruiter", "jobs"] as const;
export const RECRUITER_JOB_STATISTICS_QUERY_KEY = ["recruiter", "jobs", "statistics"] as const;

export function useMyCompanyJobs(params: { page: number; size: number; sort?: string }) {
  return useQuery({
    queryKey: [...RECRUITER_JOBS_QUERY_KEY, "list", params],
    queryFn: () => recruiterJobService.getMyCompanyJobs(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

export function useRecruiterJobStatistics() {
  return useQuery({
    queryKey: RECRUITER_JOB_STATISTICS_QUERY_KEY,
    queryFn: () => recruiterJobService.getMyCompanyStatistics().then((res) => res.data),
  });
}

export function useJobPreview(jobId: string | undefined) {
  return useQuery({
    queryKey: [...RECRUITER_JOBS_QUERY_KEY, "preview", jobId],
    queryFn: () => recruiterJobService.preview(jobId as string).then((res) => res.data),
    enabled: !!jobId,
  });
}

function invalidateJobLists(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: RECRUITER_JOBS_QUERY_KEY });
}

export function useCreateJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: JobFormRequest) => recruiterJobService.create(payload),
    onSuccess: (response) => {
      invalidateJobLists(queryClient);
      toast.success(response.message || "Job created successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ jobId, payload }: { jobId: string; payload: JobFormRequest }) =>
      recruiterJobService.update(jobId, payload),
    onSuccess: (response) => {
      invalidateJobLists(queryClient);
      toast.success(response.message || "Job updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => recruiterJobService.remove(jobId),
    onSuccess: (response) => {
      invalidateJobLists(queryClient);
      toast.success(response.message || "Job deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function usePublishJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => recruiterJobService.publish(jobId),
    onSuccess: (response) => {
      invalidateJobLists(queryClient);
      toast.success(response.message || "Job published successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useCloseJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => recruiterJobService.close(jobId),
    onSuccess: (response) => {
      invalidateJobLists(queryClient);
      toast.success(response.message || "Job closed successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useReopenJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => recruiterJobService.reopen(jobId),
    onSuccess: (response) => {
      invalidateJobLists(queryClient);
      toast.success(response.message || "Job reopened successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDuplicateJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => recruiterJobService.duplicate(jobId),
    onSuccess: (response) => {
      invalidateJobLists(queryClient);
      toast.success(response.message || "Job duplicated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
