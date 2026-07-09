// Mirrors job-service recruiter-management DTOs (POST/PUT /jobs/me/**) exactly.
// Reuses public browsing types/enums from features/jobs/types where identical.
import type {
  Currency,
  ExperienceLevel,
  JobType,
  RequiredProficiency,
  RequirementType,
  SalaryType,
  WorkMode,
} from "@/features/jobs/types";

export interface JobLocationRequest {
  city: string;
  state: string | null;
  country: string;
}

export interface JobSkillRequest {
  name: string;
  requiredProficiency: RequiredProficiency;
  mandatory: boolean;
}

export interface JobBenefitRequest {
  title: string;
  description: string | null;
}

export interface JobRequirementRequest {
  type: RequirementType;
  description: string;
  displayOrder: number;
}

/** POST /jobs/me and PUT /jobs/me/{jobId} share this exact shape. */
export interface JobFormRequest {
  categoryId: string;
  title: string;
  description: string;
  jobType: JobType;
  experienceLevel: ExperienceLevel;
  workMode: WorkMode;
  minSalary: number | null;
  maxSalary: number | null;
  salaryType: SalaryType | null;
  currency: Currency | null;
  vacancies: number;
  applicationDeadline: string | null;
  locations: JobLocationRequest[];
  skills: JobSkillRequest[];
  benefits: JobBenefitRequest[];
  requirements: JobRequirementRequest[];
}

/** GET /jobs/me/statistics. */
export interface JobStatisticsResponse {
  companyId: string;
  totalJobs: number;
  activeJobs: number;
  closedJobs: number;
  draftJobs: number;
}
