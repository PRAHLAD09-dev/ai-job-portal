import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { adminJobService, type AdminJobSearchParams } from "@/features/admin/services/admin-job.service";
import { extractErrorMessage } from "@/services/api-client";

export const ADMIN_JOBS_QUERY_KEY = ["admin", "jobs"] as const;

export function useAdminJobs(params: AdminJobSearchParams) {
  return useQuery({
    queryKey: [...ADMIN_JOBS_QUERY_KEY, "list", params],
    queryFn: () => adminJobService.search(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

function invalidateJobs(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: ADMIN_JOBS_QUERY_KEY });
}

export function useRemoveJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => adminJobService.remove(jobId),
    onSuccess: (response) => {
      invalidateJobs(queryClient);
      toast.success(response.message || "Job removed successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useRestoreJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => adminJobService.restore(jobId),
    onSuccess: (response) => {
      invalidateJobs(queryClient);
      toast.success(response.message || "Job restored successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useFeatureJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => adminJobService.feature(jobId),
    onSuccess: (response) => {
      invalidateJobs(queryClient);
      toast.success(response.message || "Job featured successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUnfeatureJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (jobId: string) => adminJobService.unfeature(jobId),
    onSuccess: (response) => {
      invalidateJobs(queryClient);
      toast.success(response.message || "Job unfeatured successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
