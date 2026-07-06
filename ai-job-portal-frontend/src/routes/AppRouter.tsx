import { lazy, Suspense } from "react";
import { Routes, Route } from "react-router-dom";
import { PageLoader } from "@/components/common/PageLoader";
import { PublicRoute } from "@/routes/PublicRoute";
import { ProtectedRoute } from "@/routes/ProtectedRoute";
import { RoleRoute } from "@/routes/RoleRoute";

import { GuestLayout } from "@/layouts/GuestLayout";
import { AuthLayout } from "@/layouts/AuthLayout";
import { CandidateLayout } from "@/layouts/CandidateLayout";
import { RecruiterLayout } from "@/layouts/RecruiterLayout";
import { AdminLayout } from "@/layouts/AdminLayout";
import { ErrorLayout } from "@/layouts/ErrorLayout";

// ---- Lazy-loaded pages (code splitting, per performance requirements) ----
const HomePage = lazy(() => import("@/pages/guest/HomePage"));
const JobsPage = lazy(() => import("@/pages/guest/JobsPage"));

const LoginPage = lazy(() => import("@/features/auth/pages/LoginPage"));
const RegisterPage = lazy(() => import("@/features/auth/pages/RegisterPage"));
const ForgotPasswordPage = lazy(() => import("@/features/auth/pages/ForgotPasswordPage"));
const ResetPasswordPage = lazy(() => import("@/features/auth/pages/ResetPasswordPage"));
const VerifyEmailPage = lazy(() => import("@/features/auth/pages/VerifyEmailPage"));

const CandidateDashboardPage = lazy(() => import("@/pages/candidate/CandidateDashboardPage"));
const CandidateProfilePage = lazy(() => import("@/pages/candidate/CandidateProfilePage"));
const CandidateJobsPage = lazy(() => import("@/pages/candidate/CandidateJobsPage"));
const JobDetailsPage = lazy(() => import("@/pages/candidate/JobDetailsPage"));
const SavedJobsPage = lazy(() => import("@/pages/candidate/SavedJobsPage"));
const CandidateApplicationsPage = lazy(() => import("@/pages/candidate/CandidateApplicationsPage"));
const ApplicationDetailsPage = lazy(() => import("@/pages/candidate/ApplicationDetailsPage"));
const CandidateAiPage = lazy(() => import("@/pages/candidate/CandidateAiPage"));
const CandidateSettingsPage = lazy(() => import("@/pages/candidate/CandidateSettingsPage"));

const RecruiterDashboardPage = lazy(() => import("@/pages/recruiter/RecruiterDashboardPage"));
const RecruiterCompanyPage = lazy(() => import("@/pages/recruiter/RecruiterCompanyPage"));
const RecruiterJobsPage = lazy(() => import("@/pages/recruiter/RecruiterJobsPage"));
const RecruiterCandidatesPage = lazy(() => import("@/pages/recruiter/RecruiterCandidatesPage"));
const RecruiterAiPage = lazy(() => import("@/pages/recruiter/RecruiterAiPage"));

const AdminDashboardPage = lazy(() => import("@/pages/admin/AdminDashboardPage"));
const AdminUsersPage = lazy(() => import("@/pages/admin/AdminUsersPage"));
const AdminCompaniesPage = lazy(() => import("@/pages/admin/AdminCompaniesPage"));
const AdminJobsPage = lazy(() => import("@/pages/admin/AdminJobsPage"));

const UnauthorizedPage = lazy(() => import("@/pages/errors/UnauthorizedPage"));
const ForbiddenPage = lazy(() => import("@/pages/errors/ForbiddenPage"));
const NotFoundPage = lazy(() => import("@/pages/errors/NotFoundPage"));
const ServerErrorPage = lazy(() => import("@/pages/errors/ServerErrorPage"));

export function AppRouter() {
  return (
    <Suspense fallback={<PageLoader />}>
      <Routes>
        {/* ---- Guest routes ---- */}
        <Route element={<GuestLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/jobs" element={<JobsPage />} />
        </Route>

        {/* ---- Auth routes (guest-only) ---- */}
        <Route element={<PublicRoute />}>
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="/reset-password" element={<ResetPasswordPage />} />
          </Route>
        </Route>

        {/* Email verification is reachable whether logged in or not */}
        <Route element={<AuthLayout />}>
          <Route path="/verify-email" element={<VerifyEmailPage />} />
        </Route>

        {/* ---- Candidate routes ---- */}
        <Route element={<ProtectedRoute />}>
          <Route element={<RoleRoute allowed={["CANDIDATE"]} />}>
            <Route element={<CandidateLayout />}>
              <Route path="/candidate/dashboard" element={<CandidateDashboardPage />} />
              <Route path="/candidate/profile" element={<CandidateProfilePage />} />
              <Route path="/candidate/jobs" element={<CandidateJobsPage />} />
              <Route path="/candidate/jobs/saved" element={<SavedJobsPage />} />
              <Route path="/candidate/jobs/:jobId" element={<JobDetailsPage />} />
              <Route path="/candidate/applications" element={<CandidateApplicationsPage />} />
              <Route path="/candidate/applications/:applicationId" element={<ApplicationDetailsPage />} />
              <Route path="/candidate/ai" element={<CandidateAiPage />} />
              <Route path="/candidate/settings" element={<CandidateSettingsPage />} />
            </Route>
          </Route>

          {/* ---- Recruiter routes ---- */}
          <Route element={<RoleRoute allowed={["RECRUITER"]} />}>
            <Route element={<RecruiterLayout />}>
              <Route path="/recruiter/dashboard" element={<RecruiterDashboardPage />} />
              <Route path="/recruiter/company" element={<RecruiterCompanyPage />} />
              <Route path="/recruiter/jobs" element={<RecruiterJobsPage />} />
              <Route path="/recruiter/candidates" element={<RecruiterCandidatesPage />} />
              <Route path="/recruiter/ai" element={<RecruiterAiPage />} />
            </Route>
          </Route>

          {/* ---- Admin routes ---- */}
          <Route element={<RoleRoute allowed={["ADMIN"]} />}>
            <Route element={<AdminLayout />}>
              <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
              <Route path="/admin/users" element={<AdminUsersPage />} />
              <Route path="/admin/companies" element={<AdminCompaniesPage />} />
              <Route path="/admin/jobs" element={<AdminJobsPage />} />
            </Route>
          </Route>
        </Route>

        {/* ---- Error routes ---- */}
        <Route element={<ErrorLayout />}>
          <Route path="/401" element={<UnauthorizedPage />} />
          <Route path="/403" element={<ForbiddenPage />} />
          <Route path="/500" element={<ServerErrorPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </Suspense>
  );
}
