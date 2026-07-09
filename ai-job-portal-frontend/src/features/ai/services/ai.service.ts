import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type {
  AnalyzeResumeRequest,
  ATSScoreResponse,
  CandidateRecommendationResponse,
  CoverLetterRequest,
  CoverLetterResponse,
  GenerateInterviewQuestionsRequest,
  InterviewQuestionResponse,
  JobDescriptionRequest,
  JobDescriptionResponse,
  JobRecommendationResponse,
  ResumeAnalysisResponse,
  SkillGapResponse,
} from "@/features/ai/types";

/** Maps 1:1 to ai-service RecommendationController, JobDescriptionController, InterviewQuestionController,
 * ResumeAnalysisController, CoverLetterController, SkillGapController. */
export const aiService = {
  recommendJobs: () =>
    apiClient.post<ApiResponse<JobRecommendationResponse[]>>("/ai/jobs/recommend").then((res) => res.data),

  recommendCandidates: (jobId: string) =>
    apiClient
      .post<ApiResponse<CandidateRecommendationResponse[]>>(`/ai/candidates/recommend/${jobId}`)
      .then((res) => res.data),

  generateJobDescription: (payload: JobDescriptionRequest) =>
    apiClient
      .post<ApiResponse<JobDescriptionResponse>>("/ai/job-description", payload)
      .then((res) => res.data),

  generateInterviewQuestions: (payload: GenerateInterviewQuestionsRequest) =>
    apiClient
      .post<ApiResponse<InterviewQuestionResponse[]>>("/ai/interview/questions", payload)
      .then((res) => res.data),

  getInterviewQuestionsForJob: (jobId: string) =>
    apiClient
      .get<ApiResponse<InterviewQuestionResponse[]>>(`/ai/interview/questions/${jobId}`)
      .then((res) => res.data),

  analyzeResume: (payload: AnalyzeResumeRequest) =>
    apiClient.post<ApiResponse<ResumeAnalysisResponse>>("/ai/resume/analyze", payload).then((res) => res.data),

  getLatestResumeAnalysis: () =>
    apiClient.get<ApiResponse<ResumeAnalysisResponse>>("/ai/resume/analyze/latest").then((res) => res.data),

  scoreResume: (payload: AnalyzeResumeRequest) =>
    apiClient.post<ApiResponse<ATSScoreResponse>>("/ai/resume/score", payload).then((res) => res.data),

  generateCoverLetter: (payload: CoverLetterRequest) =>
    apiClient.post<ApiResponse<CoverLetterResponse>>("/ai/cover-letter", payload).then((res) => res.data),

  getSkillGap: () => apiClient.get<ApiResponse<SkillGapResponse>>("/ai/skills/gap").then((res) => res.data),
};
