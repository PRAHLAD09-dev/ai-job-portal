// Mirrors application-service RecruiterApplicationController DTOs exactly. Do not rename fields.
import type { ApplicationStatus } from "@/features/applications/types";

export type { ApplicationStatus };

/** GET /recruiter/applications — search/filter query params (all optional). */
export interface ApplicationSearchCriteria {
  keyword?: string;
  jobId?: string;
  status?: ApplicationStatus;
  appliedAfter?: string;
  appliedBefore?: string;
}

/** PATCH /recruiter/applications/{id}/status and the shortlist/interview/offer/hire/reject convenience endpoints. */
export interface UpdateApplicationStatusRequest {
  status: ApplicationStatus;
  interviewDate: string | null;
  remarks: string | null;
}

/** PUT /recruiter/applications/{id}/notes. */
export interface RecruiterNotesRequest {
  notes: string;
}

/** GET /recruiter/applications/statistics — hiring pipeline / analytics widgets. */
export interface ApplicationStatisticsResponse {
  companyId: string;
  totalApplications: number;
  countByStatus: Record<string, number>;
}
