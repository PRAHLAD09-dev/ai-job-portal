/**
 * Route path constants, mirroring 02_FRONTEND_ARCHITECTURE.md exactly.
 * Never inline route strings in components — import from here.
 */
export const ROUTES = {
  // Guest
  HOME: "/",
  JOBS: "/jobs",
  LOGIN: "/login",
  REGISTER: "/register",
  FORGOT_PASSWORD: "/forgot-password",
  RESET_PASSWORD: "/reset-password",
  VERIFY_EMAIL: "/verify-email",

  // Candidate
  CANDIDATE_DASHBOARD: "/candidate/dashboard",
  CANDIDATE_PROFILE: "/candidate/profile",
  CANDIDATE_JOBS: "/candidate/jobs",
  CANDIDATE_SAVED_JOBS: "/candidate/jobs/saved",
  CANDIDATE_APPLICATIONS: "/candidate/applications",
  CANDIDATE_AI: "/candidate/ai",
  CANDIDATE_SETTINGS: "/candidate/settings",

  // Recruiter
  RECRUITER_DASHBOARD: "/recruiter/dashboard",
  RECRUITER_COMPANY: "/recruiter/company",
  RECRUITER_JOBS: "/recruiter/jobs",
  RECRUITER_CANDIDATES: "/recruiter/candidates",
  RECRUITER_AI: "/recruiter/ai",

  // Admin
  ADMIN_DASHBOARD: "/admin/dashboard",
  ADMIN_USERS: "/admin/users",
  ADMIN_COMPANIES: "/admin/companies",
  ADMIN_JOBS: "/admin/jobs",

  // Errors
  UNAUTHORIZED_401: "/401",
  FORBIDDEN_403: "/403",
  NOT_FOUND_404: "/404",
  SERVER_ERROR_500: "/500",
} as const;

/** Dynamic route builders — kept alongside ROUTES so components never hand-build paths. */
export const buildRoute = {
  candidateJobDetails: (jobId: string) => `/candidate/jobs/${jobId}`,
  candidateApplicationDetails: (applicationId: string) => `/candidate/applications/${applicationId}`,
};
