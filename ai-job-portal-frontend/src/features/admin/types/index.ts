// Mirrors admin-service DTOs/enums exactly (com.prahlad.aijobportal.adminservice.*). Do not rename fields.

export interface AdminUserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  status: string;
  emailVerified: boolean;
  accountLocked: boolean;
  failedLoginAttempts: number;
  createdAt: string;
  updatedAt: string;
}

export interface AdminCompanyResponse {
  id: string;
  name: string;
  slug: string;
  industry: string;
  companySize: string;
  websiteUrl: string | null;
  email: string | null;
  logoUrl: string | null;
  verificationStatus: string;
  activeJobCount: number;
  totalHires: number;
  createdAt: string;
  updatedAt: string;
}

export interface AdminJobResponse {
  id: string;
  companyId: string;
  companyName: string;
  title: string;
  slug: string;
  jobType: string;
  workMode: string;
  status: string;
  featured: boolean;
  viewCount: number;
  publishedAt: string | null;
  closedAt: string | null;
  createdAt: string;
}

export interface UserStatisticsResponse {
  totalUsers: number;
  totalCandidates: number;
  totalRecruiters: number;
  totalAdmins: number;
  activeUsers: number;
  disabledUsers: number;
  pendingVerificationUsers: number;
}

export interface CompanyStatisticsResponse {
  totalCompanies: number;
  pendingCompanies: number;
  verifiedCompanies: number;
  rejectedCompanies: number;
  suspendedCompanies: number;
}

export interface JobStatisticsResponse {
  totalJobs: number;
  draftJobs: number;
  publishedJobs: number;
  closedJobs: number;
  archivedJobs: number;
  featuredJobs: number;
}

export interface ApplicationStatisticsResponse {
  totalApplications: number;
  appliedCount: number;
  underReviewCount: number;
  shortlistedCount: number;
  interviewCount: number;
  offeredCount: number;
  hiredCount: number;
  rejectedCount: number;
  withdrawnCount: number;
}

export interface NotificationStatisticsResponse {
  totalNotifications: number;
  pendingCount: number;
  sentCount: number;
  failedCount: number;
  readCount: number;
  unreadCount: number;
}

export interface AiStatisticsResponse {
  totalResumeAnalyses: number;
  totalJobRecommendations: number;
  totalInterviewQuestionSets: number;
}

export type AuditActionType =
  | "LOGIN"
  | "USER_ENABLED"
  | "USER_DISABLED"
  | "USER_DELETED"
  | "COMPANY_VERIFIED"
  | "COMPANY_REJECTED"
  | "COMPANY_SUSPENDED"
  | "JOB_REMOVED"
  | "JOB_RESTORED"
  | "JOB_FEATURED"
  | "JOB_UNFEATURED";

export type AuditTargetType = "USER" | "COMPANY" | "JOB" | "ADMIN_SESSION";

export interface AuditLogResponse {
  id: string;
  adminId: string;
  adminEmail: string;
  actionType: AuditActionType;
  targetType: AuditTargetType;
  targetId: string;
  description: string;
  ipAddress: string;
  createdAt: string;
}

/** A single labeled value in a distribution/breakdown chart (mirrors admin-service ChartDataPoint). */
export interface ChartDataPoint {
  label: string;
  value: number;
}

/** Mirrors admin-service UserGrowthPointResponse (Auth Service signup counts by day). */
export interface UserGrowthPointResponse {
  date: string;
  signupCount: number;
}

/** GET /admin/dashboard/charts — chart-ready data for the admin dashboard (Day 08 "Admin Dashboard: Add Charts"). */
export interface DashboardChartsResponse {
  userGrowth: UserGrowthPointResponse[];
  companyVerification: ChartDataPoint[];
  jobsByStatus: ChartDataPoint[];
  applicationsByStatus: ChartDataPoint[];
  aiUsageByFeature: ChartDataPoint[];
}

/** GET /admin/dashboard — aggregated platform statistics + recent activity. */
export interface DashboardResponse {
  userStatistics: UserStatisticsResponse;
  companyStatistics: CompanyStatisticsResponse;
  jobStatistics: JobStatisticsResponse;
  applicationStatistics: ApplicationStatisticsResponse;
  aiStatistics: AiStatisticsResponse;
  notificationStatistics: NotificationStatisticsResponse;
  recentActivity: AuditLogResponse[];
}
