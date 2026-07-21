// Mirrors ai-service DTOs exactly. Do not rename fields.

/** Shared explainable, per-dimension breakdown (0-100 each) behind an overall AI match score. */
export interface MatchBreakdownResponse {
  skillMatch: number;
  experienceMatch: number;
  educationMatch: number;
  projectMatch: number;
  salaryMatch: number;
  locationMatch: number;
}

/** POST /ai/candidates/recommend/{jobId} — recruiter-facing ranked applicants for one job. */
export interface CandidateRecommendationResponse {
  applicationId: string;
  candidateId: string;
  candidateName: string;
  matchScore: number;
  matchBreakdown: MatchBreakdownResponse;
  reasoning: string[];
  strengths: string[];
  weaknesses: string[];
  missingSkills: string[];
  hiringRecommendation: "Strongly Recommend" | "Recommend" | "Consider" | "Not a Fit";
}

/** POST /ai/jobs/recommend — candidate-facing job matches (used later by candidate AI page). */
export interface JobRecommendationResponse {
  jobId: string;
  jobTitle: string;
  companyName: string;
  matchScore: number;
  matchBreakdown: MatchBreakdownResponse;
  reasoning: string[];
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

/**
 * POST /ai/resume/analyze and /ai/resume/score both send this. Per
 * DAY10_AI_Enhancement_ATS_Intelligence.md's "Resume Extraction
 * Improvements": the candidate supplies only the resume PDF's
 * Cloudinary URL — ai-service downloads it and extracts text
 * server-side (Apache PDFBox). Never send resumeText from the
 * frontend; the backend no longer accepts it.
 */
export interface AnalyzeResumeRequest {
  resumeUrl: string;
}

/**
 * POST /ai/resume/analyze (201) and GET /ai/resume/analyze/latest.
 *
 * professionalSummary/projects/certifications/languages/achievements are
 * AI-extracted directly from the resume text on a fresh analysis only —
 * they are not persisted backend-side, so a response served from a
 * duplicate-resume-text hit or from the "latest" endpoint will carry
 * these as null/empty. Render them defensively.
 */
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
  professionalSummary: string | null;
  projects: string[] | null;
  certifications: string[] | null;
  languages: string[] | null;
  achievements: string[] | null;
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

export interface SkillPriorityOrderResponse {
  highPriority: string[];
  mediumPriority: string[];
  lowPriority: string[];
}

/** GET /ai/skills/gap. */
export interface SkillGapResponse {
  currentSkills: string[];
  missingSkills: string[];
  careerSuggestions: string[];
  priorityOrder: SkillPriorityOrderResponse;
  learningSuggestions: string[];
}

/** GET /ai/learning-roadmap. */
export interface LearningRoadmapResponse {
  beginnerTopics: string[];
  intermediateTopics: string[];
  advancedTopics: string[];
  suggestedResources: string[];
  practiceOrder: string[];
}

/** Candidate-facing AI Interview Generator — practice questions built from the candidate's own resume. */
export type PrepDifficulty = "EASY" | "MEDIUM" | "HARD";
export type PrepQuestionType = "TECHNICAL" | "HR" | "PROJECT_BASED" | "MIXED";

/** GET /ai/interview-prep/topics — skills/projects detected from the candidate's latest resume analysis. */
export interface DetectedTopicsResponse {
  skills: string[];
  projects: string[];
}

/** POST /ai/interview-prep/generate. selectedTopics should be a subset of (or equal to) DetectedTopicsResponse's topics. */
export interface GenerateInterviewPrepRequest {
  selectedTopics: string[];
  difficulty?: PrepDifficulty | null;
  questionCount?: number | null;
  questionType?: PrepQuestionType | null;
}

/** POST /ai/interview-prep/generate (201) and GET /ai/interview-prep/latest. Questions grouped by topic. */
export interface InterviewPrepQuestionSetResponse {
  id: string;
  selectedTopics: string[];
  difficulty: string;
  questionType: string;
  totalQuestions: number;
  sections: { topic: string; questions: string[] }[];
  generatedAt: string;
}

