import type { JobSavedCountResponse } from "@/features/jobs/types";
import type { JobStatisticsResponse } from "@/features/recruiter-jobs/types";
import type { ApplicationStatisticsResponse, ApplicationStatus } from "@/features/recruiter-applications/types";

/**
 * DAY11 "Recruiter Dashboard Improvements" — GET /recruiter/dashboard.
 * Aggregated live from Job Service, Application Service, and AI Service;
 * nothing here is persisted or duplicated in recruiter-service's own DB.
 */
export interface RecruiterDashboardResponse {
  jobStatistics: JobStatisticsResponse;
  applicationStatistics: ApplicationStatisticsResponse;
  savedJobStatistics: JobSavedCountResponse[];
  recentApplications: RecentApplicationInsightResponse[];
}

/**
 * One row of the "recent applications" table: application summary
 * (including viewed status) joined with the candidate's AI Match score.
 * `aiMatchScore` is `null` when the candidate has no resume analysis yet
 * — a normal state, not an error.
 */
export interface RecentApplicationInsightResponse {
  applicationId: string;
  candidateId: string;
  candidateName: string;
  jobId: string;
  jobTitle: string;
  status: ApplicationStatus;
  appliedAt: string;
  viewed: boolean;
  aiMatchScore: number | null;
}
