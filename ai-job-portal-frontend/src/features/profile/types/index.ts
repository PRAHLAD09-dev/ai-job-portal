// Mirrors candidate-service enums/DTOs exactly. Do not rename fields.

export type ProfileVisibility = "PUBLIC" | "PRIVATE";

export type DegreeType =
  | "HIGH_SCHOOL"
  | "DIPLOMA"
  | "ASSOCIATE"
  | "BACHELOR"
  | "MASTER"
  | "DOCTORATE"
  | "CERTIFICATION"
  | "OTHER";

export type EmploymentType = "FULL_TIME" | "PART_TIME" | "CONTRACT" | "INTERNSHIP" | "FREELANCE";

export type SkillProficiency = "BEGINNER" | "INTERMEDIATE" | "ADVANCED" | "EXPERT";

export type ResumeStatus = "ACTIVE" | "ARCHIVED";

// ---- Education (candidate-service EducationController) ----
export interface EducationResponse {
  id: string;
  institutionName: string;
  degreeType: DegreeType;
  fieldOfStudy: string;
  startDate: string;
  endDate: string | null;
  currentlyStudying: boolean;
  grade: string | null;
  description: string | null;
}

export interface EducationRequest {
  institutionName: string;
  degreeType: DegreeType;
  fieldOfStudy: string;
  startDate: string;
  endDate: string | null;
  currentlyStudying: boolean;
  grade: string | null;
  description: string | null;
}

// ---- Experience (candidate-service ExperienceController) ----
export interface ExperienceResponse {
  id: string;
  companyName: string;
  jobTitle: string;
  employmentType: EmploymentType;
  location: string | null;
  startDate: string;
  endDate: string | null;
  currentlyWorking: boolean;
  description: string | null;
}

export interface ExperienceRequest {
  companyName: string;
  jobTitle: string;
  employmentType: EmploymentType;
  location: string | null;
  startDate: string;
  endDate: string | null;
  currentlyWorking: boolean;
  description: string | null;
}

// ---- Skill (candidate-service SkillController) ----
export interface SkillResponse {
  id: string;
  name: string;
  proficiency: SkillProficiency;
  yearsOfExperience: number | null;
}

export interface SkillRequest {
  name: string;
  proficiency: SkillProficiency;
  yearsOfExperience: number | null;
}

// ---- Resume (candidate-service ResumeController) ----
export interface ResumeResponse {
  id: string;
  fileName: string;
  fileUrl: string;
  fileFormat: string;
  fileSizeBytes: number;
  versionNumber: number;
  status: ResumeStatus;
  createdAt: string;
}

// ---- Candidate profile (candidate-service CandidateController) ----
export interface CandidateProfileResponse {
  id: string;
  userId: string;
  email: string;
  fullName: string;
  headline: string | null;
  summary: string | null;
  phoneNumber: string | null;
  dateOfBirth: string | null;
  city: string | null;
  state: string | null;
  country: string | null;
  portfolioUrl: string | null;
  linkedinUrl: string | null;
  githubUrl: string | null;
  visibility: ProfileVisibility;
  profileCompletionPercentage: number;
  educations: EducationResponse[];
  experiences: ExperienceResponse[];
  skills: SkillResponse[];
  resumes: ResumeResponse[];
}

export interface CreateCandidateProfileRequest {
  headline: string | null;
  summary: string | null;
  phoneNumber: string | null;
  dateOfBirth: string | null;
  city: string | null;
  state: string | null;
  country: string | null;
  portfolioUrl: string | null;
  linkedinUrl: string | null;
  githubUrl: string | null;
  visibility: ProfileVisibility;
}

export type UpdateCandidateProfileRequest = CreateCandidateProfileRequest;

export interface ProfileCompletionResponse {
  profileCompletionPercentage: number;
}
