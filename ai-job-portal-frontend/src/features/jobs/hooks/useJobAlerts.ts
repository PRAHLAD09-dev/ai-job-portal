import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { jobAlertService } from "@/features/jobs/services/job-alert.service";
import { extractErrorMessage } from "@/services/api-client";
import type { JobAlertRequest } from "@/features/jobs/types";

export const JOB_ALERTS_QUERY_KEY = ["jobs", "alerts"] as const;

export function useJobAlerts() {
  return useQuery({
    queryKey: JOB_ALERTS_QUERY_KEY,
    queryFn: () => jobAlertService.getMyAlerts().then((res) => res.data),
  });
}

export function useCreateJobAlert() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: JobAlertRequest) => jobAlertService.create(payload),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: JOB_ALERTS_QUERY_KEY });
      toast.success(response.message || "Job alert created successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useUpdateJobAlert() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ alertId, payload }: { alertId: string; payload: JobAlertRequest }) =>
      jobAlertService.update(alertId, payload),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: JOB_ALERTS_QUERY_KEY });
      toast.success(response.message || "Job alert updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteJobAlert() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (alertId: string) => jobAlertService.remove(alertId),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: JOB_ALERTS_QUERY_KEY });
      toast.success(response.message || "Job alert deleted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
