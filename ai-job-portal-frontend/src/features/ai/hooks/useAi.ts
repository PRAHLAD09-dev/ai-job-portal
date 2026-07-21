import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { aiService } from "@/features/ai/services/ai.service";
import { extractErrorMessage } from "@/services/api-client";
import type {
  AnalyzeResumeRequest,
  CoverLetterRequest,
  GenerateInterviewPrepRequest,
  GenerateInterviewQuestionsRequest,
  JobDescriptionRequest,
} from "@/features/ai/types";

export const AI_RESUME_ANALYSIS_LATEST_KEY = ["ai", "resume-analysis", "latest"] as const;
export const AI_SKILL_GAP_KEY = ["ai", "skill-gap"] as const;
export const AI_JOB_RECOMMENDATIONS_KEY = ["ai", "job-recommendations"] as const;
export const AI_LEARNING_ROADMAP_KEY = ["ai", "learning-roadmap"] as const;

/** Candidate-facing job matches — POST endpoint under the hood but treated as cacheable read data. */
export function useJobRecommendations(enabled: boolean) {
  return useQuery({
    queryKey: AI_JOB_RECOMMENDATIONS_KEY,
    queryFn: () => aiService.recommendJobs().then((res) => res.data),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
}

/**
 * Single-job AI match score for the job detail page. A 404 means the
 * candidate hasn't run "AI Match" yet (or this job predates their last
 * run) — that's a normal empty state, not an error toast, so retry and
 * the global error toast are both suppressed; the caller should check
 * isNotFoundError(error) to show a "Generate my match score" CTA.
 */
export function useJobMatch(jobId: string | undefined) {
  return useQuery({
    queryKey: ["ai", "job-match", jobId],
    queryFn: () => aiService.getJobMatch(jobId as string).then((res) => res.data),
    enabled: !!jobId,
    retry: false,
    meta: { suppressErrorToast: true },
  });
}

/** Triggers a fresh recommend run (e.g. from the "Generate my match score" CTA), then refreshes this job's match. */
export function useGenerateJobMatch(jobId: string | undefined) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => aiService.recommendJobs().then((res) => res.data),
    onSuccess: (data) => {
      queryClient.setQueryData(AI_JOB_RECOMMENDATIONS_KEY, data);
      queryClient.invalidateQueries({ queryKey: ["ai", "job-match", jobId] });
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

/** 404 ("no analysis yet") is a normal empty state here, not an error toast — see queryCache in query-client.ts. */
export function useLatestResumeAnalysis() {
  return useQuery({
    queryKey: AI_RESUME_ANALYSIS_LATEST_KEY,
    queryFn: () => aiService.getLatestResumeAnalysis().then((res) => res.data),
    retry: false,
    meta: { suppressErrorToast: true },
  });
}

export function useAnalyzeResume() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: AnalyzeResumeRequest) => aiService.analyzeResume(payload).then((res) => res.data),
    onSuccess: (data) => {
      queryClient.setQueryData(AI_RESUME_ANALYSIS_LATEST_KEY, data);
      toast.success("Resume analyzed successfully");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useScoreResume() {
  return useMutation({
    mutationFn: (payload: AnalyzeResumeRequest) => aiService.scoreResume(payload).then((res) => res.data),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useGenerateCoverLetter() {
  return useMutation({
    mutationFn: (payload: CoverLetterRequest) => aiService.generateCoverLetter(payload).then((res) => res.data),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useSkillGap(enabled: boolean) {
  return useQuery({
    queryKey: AI_SKILL_GAP_KEY,
    queryFn: () => aiService.getSkillGap().then((res) => res.data),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
}

/** Beginner-to-advanced learning path for the candidate — GET /ai/learning-roadmap. */
export function useLearningRoadmap(enabled: boolean) {
  return useQuery({
    queryKey: AI_LEARNING_ROADMAP_KEY,
    queryFn: () => aiService.getLearningRoadmap().then((res) => res.data),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
}

export function useCandidateRecommendations(jobId: string | undefined) {
  return useMutation({
    mutationFn: (id: string) => aiService.recommendCandidates(id).then((res) => res.data),
    onError: (error) => toast.error(extractErrorMessage(error)),
    mutationKey: ["ai", "candidate-recommendations", jobId],
  });
}

export function useGenerateJobDescription() {
  return useMutation({
    mutationFn: (payload: JobDescriptionRequest) => aiService.generateJobDescription(payload).then((res) => res.data),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useGenerateInterviewQuestions() {
  return useMutation({
    mutationFn: (payload: GenerateInterviewQuestionsRequest) =>
      aiService.generateInterviewQuestions(payload).then((res) => res.data),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useInterviewQuestionsForJob(jobId: string | undefined) {
  return useQuery({
    queryKey: ["ai", "interview-questions", jobId],
    queryFn: () => aiService.getInterviewQuestionsForJob(jobId as string).then((res) => res.data),
    enabled: !!jobId,
  });
}

export const AI_INTERVIEW_PREP_LATEST_KEY = ["ai", "interview-prep", "latest"] as const;
export const AI_INTERVIEW_PREP_TOPICS_KEY = ["ai", "interview-prep", "topics"] as const;

/**
 * Skills/projects detected from the candidate's resume, used to build the
 * topic-selection chips. A 404 here means the candidate hasn't run resume
 * analysis yet (ai-service derives topics from it) — a normal empty state,
 * not an error toast; the caller should check isNotFoundError(error) and
 * point the candidate at the Resume tab.
 */
export function useInterviewPrepTopics() {
  return useQuery({
    queryKey: AI_INTERVIEW_PREP_TOPICS_KEY,
    queryFn: () => aiService.detectInterviewPrepTopics().then((res) => res.data),
    retry: false,
    meta: { suppressErrorToast: true },
  });
}

/** Most recently generated practice question set. 404 = none generated yet, a normal empty state. */
export function useLatestInterviewPrep() {
  return useQuery({
    queryKey: AI_INTERVIEW_PREP_LATEST_KEY,
    queryFn: () => aiService.getLatestInterviewPrep().then((res) => res.data),
    retry: false,
    meta: { suppressErrorToast: true },
  });
}

/** Generates a fresh set (also used for "Regenerate" — just resubmit the same payload). */
export function useGenerateInterviewPrep() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: GenerateInterviewPrepRequest) =>
      aiService.generateInterviewPrep(payload).then((res) => res.data),
    onSuccess: (data) => {
      queryClient.setQueryData(AI_INTERVIEW_PREP_LATEST_KEY, data);
      toast.success("Interview questions generated");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

/** Downloads a question set as a PDF and triggers the browser's save dialog. */
export function useDownloadInterviewPrepPdf() {
  return useMutation({
    mutationFn: (questionSetId: string) => aiService.downloadInterviewPrepPdf(questionSetId),
    onSuccess: (blob) => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "ai-interview-questions.pdf";
      a.click();
      URL.revokeObjectURL(url);
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
