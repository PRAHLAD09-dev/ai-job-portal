import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { recruiterApplicationService } from "@/features/recruiter-applications/services/recruiter-application.service";
import { extractErrorMessage } from "@/services/api-client";
import type { RecruiterApplicationListParams } from "@/features/recruiter-applications/services/recruiter-application.service";
import type { RecruiterNotesRequest, UpdateApplicationStatusRequest } from "@/features/recruiter-applications/types";

export const RECRUITER_APPLICATIONS_QUERY_KEY = ["recruiter", "applications"] as const;
export const RECRUITER_APPLICATION_STATISTICS_QUERY_KEY = ["recruiter", "applications", "statistics"] as const;

export function useRecruiterApplications(params: RecruiterApplicationListParams) {
  return useQuery({
    queryKey: [...RECRUITER_APPLICATIONS_QUERY_KEY, "list", params],
    queryFn: () => recruiterApplicationService.getApplications(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

export function useRecruiterApplicationDetail(applicationId: string | undefined) {
  return useQuery({
    queryKey: [...RECRUITER_APPLICATIONS_QUERY_KEY, "detail", applicationId],
    queryFn: () => recruiterApplicationService.getApplicationDetail(applicationId as string).then((res) => res.data),
    enabled: !!applicationId,
  });
}

export function useRecruiterApplicationTimeline(applicationId: string | undefined) {
  return useQuery({
    queryKey: [...RECRUITER_APPLICATIONS_QUERY_KEY, "timeline", applicationId],
    queryFn: () => recruiterApplicationService.getTimeline(applicationId as string).then((res) => res.data),
    enabled: !!applicationId,
  });
}

export function useRecruiterApplicationStatistics() {
  return useQuery({
    queryKey: RECRUITER_APPLICATION_STATISTICS_QUERY_KEY,
    queryFn: () => recruiterApplicationService.getStatistics().then((res) => res.data),
  });
}

function invalidateApplication(queryClient: ReturnType<typeof useQueryClient>, applicationId: string) {
  queryClient.invalidateQueries({ queryKey: RECRUITER_APPLICATIONS_QUERY_KEY });
  queryClient.invalidateQueries({ queryKey: [...RECRUITER_APPLICATIONS_QUERY_KEY, "detail", applicationId] });
  queryClient.invalidateQueries({ queryKey: [...RECRUITER_APPLICATIONS_QUERY_KEY, "timeline", applicationId] });
  queryClient.invalidateQueries({ queryKey: RECRUITER_APPLICATION_STATISTICS_QUERY_KEY });
}

/** Generic status transition — powers drag-and-drop pipeline stage moves. */
export function useUpdateApplicationStatus() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ applicationId, payload }: { applicationId: string; payload: UpdateApplicationStatusRequest }) =>
      recruiterApplicationService.updateStatus(applicationId, payload),
    onSuccess: (response, variables) => {
      invalidateApplication(queryClient, variables.applicationId);
      toast.success(response.message || "Application status updated successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useShortlistApplication() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (applicationId: string) => recruiterApplicationService.shortlist(applicationId),
    onSuccess: (response, applicationId) => {
      invalidateApplication(queryClient, applicationId);
      toast.success(response.message || "Candidate shortlisted");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useReviewApplication() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (applicationId: string) => recruiterApplicationService.review(applicationId),
    onSuccess: (response, applicationId) => {
      invalidateApplication(queryClient, applicationId);
      toast.success(response.message || "Application moved to review");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useScheduleInterview() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ applicationId, interviewDate }: { applicationId: string; interviewDate: string }) =>
      recruiterApplicationService.scheduleInterview(applicationId, interviewDate),
    onSuccess: (response, variables) => {
      invalidateApplication(queryClient, variables.applicationId);
      toast.success(response.message || "Interview scheduled");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useOfferApplication() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (applicationId: string) => recruiterApplicationService.offer(applicationId),
    onSuccess: (response, applicationId) => {
      invalidateApplication(queryClient, applicationId);
      toast.success(response.message || "Offer extended");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useHireApplication() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (applicationId: string) => recruiterApplicationService.hire(applicationId),
    onSuccess: (response, applicationId) => {
      invalidateApplication(queryClient, applicationId);
      toast.success(response.message || "Candidate hired");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useRejectApplication() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ applicationId, reason }: { applicationId: string; reason?: string }) =>
      recruiterApplicationService.reject(applicationId, reason),
    onSuccess: (response, variables) => {
      invalidateApplication(queryClient, variables.applicationId);
      toast.success(response.message || "Application rejected");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useAddApplicationNotes() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ applicationId, payload }: { applicationId: string; payload: RecruiterNotesRequest }) =>
      recruiterApplicationService.addNotes(applicationId, payload),
    onSuccess: (response, variables) => {
      invalidateApplication(queryClient, variables.applicationId);
      toast.success(response.message || "Notes saved successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
