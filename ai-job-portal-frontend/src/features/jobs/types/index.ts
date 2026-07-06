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
