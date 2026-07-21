import { apiClient } from "@/services/api-client";
import type { ApiResponse } from "@/types/api";
import type {
  AnalyzeResumeRequest,
  ATSScoreResponse,
  CandidateRecommendationResponse,
  CoverLetterRequest,
  CoverLetterResponse,
  DetectedTopicsResponse,
  GenerateInterviewPrepRequest,
  GenerateInterviewQuestionsRequest,
  InterviewPrepQuestionSetResponse,
  InterviewQuestionResponse,
  JobDescriptionRequest,
  JobDescriptionResponse,
  JobRecommendationResponse,
  LearningRoadmapResponse,
  ResumeAnalysisResponse,
  SkillGapResponse,
} from "@/features/ai/types";

/** Maps 1:1 to ai-service RecommendationController, JobDescriptionController, InterviewQuestionController,
 * ResumeAnalysisController, CoverLetterController, SkillGapController, LearningRoadmapController. */
export const aiService = {
  recommendJobs: () =>
    apiClient.post<ApiResponse<JobRecommendationResponse[]>>("/ai/jobs/recommend").then((res) => res.data),

  /** GET /ai/jobs/{jobId}/match — single-job score from the candidate's last recommend run. 404 = no score yet. */
  getJobMatch: (jobId: string) =>
    apiClient.get<ApiResponse<JobRecommendationResponse>>(`/ai/jobs/${jobId}/match`).then((res) => res.data),

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

  getLearningRoadmap: () =>
    apiClient.get<ApiResponse<LearningRoadmapResponse>>("/ai/learning-roadmap").then((res) => res.data),

  /** GET /ai/interview-prep/topics — 404 ("please analyze your resume first") is a normal empty state. */
  detectInterviewPrepTopics: () =>
    apiClient.get<ApiResponse<DetectedTopicsResponse>>("/ai/interview-prep/topics").then((res) => res.data),

  /** POST /ai/interview-prep/generate — also used for "Regenerate" by resubmitting the same request. */
  generateInterviewPrep: (payload: GenerateInterviewPrepRequest) =>
    apiClient
      .post<ApiResponse<InterviewPrepQuestionSetResponse>>("/ai/interview-prep/generate", payload)
      .then((res) => res.data),

  /** GET /ai/interview-prep/latest — 404 ("none generated yet") is a normal empty state. */
  getLatestInterviewPrep: () =>
    apiClient
      .get<ApiResponse<InterviewPrepQuestionSetResponse>>("/ai/interview-prep/latest")
      .then((res) => res.data),

  /** GET /ai/interview-prep/{id}/pdf — raw PDF bytes, not the usual ApiResponse<T> envelope. */
  downloadInterviewPrepPdf: (questionSetId: string) =>
    apiClient
      .get<Blob>(`/ai/interview-prep/${questionSetId}/pdf`, { responseType: "blob" })
      .then((res) => res.data),
};
