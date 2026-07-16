// Mirrors job-service enums/DTOs exactly. Do not rename fields.

export type JobType = "FULL_TIME" | "PART_TIME" | "CONTRACT" | "INTERNSHIP" | "FREELANCE" | "TEMPORARY";

export type ExperienceLevel =
  | "ENTRY_LEVEL"
  | "ASSOCIATE"
  | "MID_LEVEL"
  | "SENIOR_LEVEL"
  | "LEAD"
  | "MANAGER"
  | "EXECUTIVE";

export type WorkMode = "ON_SITE" | "REMOTE" | "HYBRID";

export type JobStatus = "DRAFT" | "PUBLISHED" | "CLOSED" | "ARCHIVED";

export type SalaryType = "HOURLY" | "MONTHLY" | "ANNUAL";

export type Currency = "USD" | "EUR" | "GBP" | "INR" | "AUD" | "CAD";

export type RequirementType = "QUALIFICATION" | "RESPONSIBILITY" | "NICE_TO_HAVE";

export type RequiredProficiency = "BEGINNER" | "INTERMEDIATE" | "ADVANCED" | "EXPERT";

/**
 * DAY11/DAY07 "Apply Methods": how a candidate applies for this job.
 * EASY_APPLY — in-app form, candidate picks a resume.
 * QUICK_APPLY — in-app, no resume picker; candidate's active resume is used automatically.
 * EXTERNAL_APPLY — no in-app application; candidate is redirected to externalApplyUrl.
 */
export type ApplyMethod = "EASY_APPLY" | "QUICK_APPLY" | "EXTERNAL_APPLY";

export interface JobCategoryResponse {
  id: string;
  name: string;
  slug: string;
}

export interface JobLocationResponse {
  id: string;
  city: string;
  state: string;
  country: string;
}

export interface JobSkillResponse {
  id: string;
  name: string;
  requiredProficiency: RequiredProficiency;
  mandatory: boolean;
}

export interface JobBenefitResponse {
  id: string;
  title: string;
  description: string | null;
}

export interface JobRequirementResponse {
  id: string;
  type: RequirementType;
  description: string;
  displayOrder: number;
}

/** Full job detail — GET /jobs/{id} and GET /jobs/slug/{slug}. */
export interface JobResponse {
  id: string;
  companyId: string;
  companyName: string;
  companyLogoUrl: string | null;
  category: JobCategoryResponse | null;
  title: string;
  slug: string;
  description: string;
  jobType: JobType;
  experienceLevel: ExperienceLevel;
  workMode: WorkMode;
  status: JobStatus;
  minSalary: number | null;
  maxSalary: number | null;
  salaryType: SalaryType | null;
  currency: Currency | null;
  vacancies: number;
  applicationDeadline: string | null;
  applyMethod: ApplyMethod;
  externalApplyUrl: string | null;
  featured: boolean;
  viewCount: number;
  publishedAt: string | null;
  locations: JobLocationResponse[];
  skills: JobSkillResponse[];
  benefits: JobBenefitResponse[];
  requirements: JobRequirementResponse[];
  createdAt: string;
}

/** Lightweight job summary — used in every list/search/pagination response. */
export interface JobSummaryResponse {
  id: string;
  companyName: string;
  companyLogoUrl: string | null;
  categoryName: string | null;
  title: string;
  slug: string;
  jobType: JobType;
  experienceLevel: ExperienceLevel;
  workMode: WorkMode;
  status: JobStatus;
  minSalary: number | null;
  maxSalary: number | null;
  salaryType: SalaryType | null;
  currency: Currency | null;
  featured: boolean;
  applyMethod: ApplyMethod;
  cities: string[];
  publishedAt: string | null;
}

/** All fields optional — bound to individual query params on GET /jobs/search. */
export interface JobSearchCriteria {
  keyword?: string;
  categoryId?: string;
  skill?: string;
  city?: string;
  state?: string;
  country?: string;
  companyId?: string;
  jobType?: JobType;
  experienceLevel?: ExperienceLevel;
  workMode?: WorkMode;
  minSalary?: number;
  maxSalary?: number;
  postedAfter?: string;
}

export interface SavedJobResponse {
  id: string;
  job: JobSummaryResponse;
  savedAt: string;
}

/** DAY11 "Saved Job Statistics" — GET /jobs/me/saved-statistics, backs the recruiter dashboard. */
export interface JobSavedCountResponse {
  jobId: string;
  jobTitle: string;
  savedCount: number;
}

export type JobAlertFrequency = "INSTANT" | "DAILY" | "WEEKLY";

/** Mirrors JobAlertRequest exactly — POST/PUT /jobs/alerts. */
export interface JobAlertRequest {
  keyword: string | null;
  categoryId: string | null;
  jobType: JobType | null;
  experienceLevel: ExperienceLevel | null;
  workMode: WorkMode | null;
  city: string | null;
  frequency: JobAlertFrequency;
}

/** Mirrors JobAlertResponse exactly — candidate's saved search alerts. */
export interface JobAlertResponse {
  id: string;
  keyword: string | null;
  categoryId: string | null;
  jobType: JobType | null;
  experienceLevel: ExperienceLevel | null;
  workMode: WorkMode | null;
  city: string | null;
  frequency: JobAlertFrequency;
  active: boolean;
}
