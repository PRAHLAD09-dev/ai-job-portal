// Mirrors application-service enums/DTOs exactly. Do not rename fields.

export type ApplicationStatus =
  | "APPLIED"
  | "UNDER_REVIEW"
  | "SHORTLISTED"
  | "INTERVIEW"
  | "OFFERED"
  | "HIRED"
  | "REJECTED"
  | "WITHDRAWN";

export interface CreateApplicationRequest {
  jobId: string;
  resumeId: string | null;
  coverLetter: string | null;
}

/** Full application detail — GET /applications/me/{id}. */
export interface ApplicationResponse {
  id: string;
  candidateId: string;
  candidateName: string;
  candidateEmail: string;
  recruiterId: string | null;
  companyId: string;
  companyName: string;
  jobId: string;
  jobTitle: string;
  resumeUrl: string | null;
  coverLetter: string | null;
  status: ApplicationStatus;
  appliedAt: string;
  interviewDate: string | null;
  notes: string | null;
  withdrawnAt: string | null;
  /** DAY11 "Viewed by Recruiter": set the first time any recruiter at the company opens this application. */
  viewed: boolean;
  viewedAt: string | null;
  viewedBy: string | null;
  createdAt: string;
  updatedAt: string;
}

/** Lightweight list-view projection — GET /applications/me. */
export interface ApplicationSummaryResponse {
  id: string;
  candidateId: string;
  candidateName: string;
  jobId: string;
  jobTitle: string;
  companyName: string;
  status: ApplicationStatus;
  appliedAt: string;
  interviewDate: string | null;
  viewed: boolean;
}

/**
 * DAY11 "Apply Methods" — GET /applications/apply-info/{jobId}. Tells the
 * frontend how to route the candidate's "Apply" click before deciding
 * whether to render the in-app apply form or redirect externally.
 */
export interface ApplyInfoResponse {
  jobId: string;
  applyMethod: "EASY_APPLY" | "QUICK_APPLY" | "EXTERNAL_APPLY";
  externalApplyUrl: string | null;
}

export interface TimelineResponse {
  id: string;
  applicationId: string;
  oldStatus: ApplicationStatus | null;
  newStatus: ApplicationStatus;
  changedBy: string | null;
  changedAt: string;
  remarks: string | null;
}
