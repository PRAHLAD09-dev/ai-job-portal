import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { applicationService } from "@/features/applications/services/application.service";
import { extractErrorMessage } from "@/services/api-client";
import type { CreateApplicationRequest } from "@/features/applications/types";

export const APPLICATIONS_QUERY_KEY = ["applications", "me"] as const;

export function useMyApplications(params: { page: number; size: number }) {
  return useQuery({
    queryKey: [...APPLICATIONS_QUERY_KEY, params],
    queryFn: () => applicationService.getMyApplications(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

export function useApplicationDetail(applicationId: string | undefined) {
  return useQuery({
    queryKey: ["applications", "detail", applicationId],
    queryFn: () => applicationService.getApplicationDetail(applicationId as string).then((res) => res.data),
    enabled: !!applicationId,
  });
}

export function useApplicationTimeline(applicationId: string | undefined) {
  return useQuery({
    queryKey: ["applications", "timeline", applicationId],
    queryFn: () => applicationService.getTimeline(applicationId as string).then((res) => res.data),
    enabled: !!applicationId,
  });
}

/** DAY11 "Apply Methods": fetched before rendering the Apply button so EXTERNAL_APPLY jobs redirect instead of opening the in-app form. */
export function useApplyInfo(jobId: string | undefined) {
  return useQuery({
    queryKey: ["applications", "apply-info", jobId],
    queryFn: () => applicationService.getApplyInfo(jobId as string).then((res) => res.data),
    enabled: !!jobId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useApplyToJob() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateApplicationRequest) => applicationService.apply(payload),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: APPLICATIONS_QUERY_KEY });
      toast.success(response.message || "Application submitted successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useWithdrawApplication() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (applicationId: string) => applicationService.withdraw(applicationId),
    onSuccess: (response, applicationId) => {
      queryClient.invalidateQueries({ queryKey: APPLICATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["applications", "detail", applicationId] });
      toast.success(response.message || "Application withdrawn");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
