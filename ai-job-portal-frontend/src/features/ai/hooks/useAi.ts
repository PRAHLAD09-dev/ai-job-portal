import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { aiService } from "@/features/ai/services/ai.service";
import { extractErrorMessage } from "@/services/api-client";
import type {
  AnalyzeResumeRequest,
  CoverLetterRequest,
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
