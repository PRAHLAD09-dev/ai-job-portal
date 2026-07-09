// Mirrors ai-service DTOs exactly. Do not rename fields.

/** POST /ai/candidates/recommend/{jobId} — recruiter-facing ranked applicants for one job. */
export interface CandidateRecommendationResponse {
  applicationId: string;
  candidateId: string;
  candidateName: string;
  matchScore: number;
  reasoning: string;
}

/** POST /ai/jobs/recommend — candidate-facing job matches (used later by candidate AI page). */
export interface JobRecommendationResponse {
  jobId: string;
  jobTitle: string;
  companyName: string;
  matchScore: number;
  reasoning: string;
}

export interface JobDescriptionRequest {
  jobTitle: string;
  jobType: string;
  experienceLevel: string;
  keyPoints: string[];
}

export interface JobDescriptionResponse {
  description: string;
  requiredSkills: string[];
}

export interface GenerateInterviewQuestionsRequest {
  jobId: string;
  count: number | null;
}

export interface InterviewQuestionResponse {
  id: string;
  jobId: string;
  question: string;
  difficulty: string;
  category: string;
}

/** POST /ai/resume/analyze and /ai/resume/score both send this (candidate-service does not extract text — see KNOWN_BACKEND_LIMITATIONS.md). */
export interface AnalyzeResumeRequest {
  resumeUrl: string;
  resumeText: string;
}

/** POST /ai/resume/analyze (201) and GET /ai/resume/analyze/latest. */
export interface ResumeAnalysisResponse {
  id: string;
  candidateId: string;
  resumeUrl: string;
  atsScore: number;
  strengths: string[];
  weaknesses: string[];
  missingSkills: string[];
  recommendations: string[];
  createdAt: string;
}

/** POST /ai/resume/score — quick, unpersisted ATS check. */
export interface ATSScoreResponse {
  atsScore: number;
  formattingIssues: string[];
  keywordGaps: string[];
}

/** POST /ai/cover-letter. */
export interface CoverLetterRequest {
  jobId: string;
  additionalNotes: string | null;
}

export interface CoverLetterResponse {
  coverLetterText: string;
}

/** GET /ai/skills/gap. */
export interface SkillGapResponse {
  currentSkills: string[];
  missingSkills: string[];
  careerSuggestions: string[];
}
