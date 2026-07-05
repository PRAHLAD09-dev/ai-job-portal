import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { ROUTES } from "@/constants/routes";
import { PageLoader } from "@/components/common/PageLoader";

/**
 * For guest-only pages (login/register). Already-authenticated users are
 * redirected to their dashboard instead of seeing the auth forms again.
 */
export function PublicRoute() {
  const { isAuthenticated, isInitializing, user } = useAuth();

  if (isInitializing) return <PageLoader />;

  if (isAuthenticated && user) {
    const target = user.roles.includes("ADMIN")
      ? ROUTES.ADMIN_DASHBOARD
      : user.roles.includes("RECRUITER")
        ? ROUTES.RECRUITER_DASHBOARD
        : ROUTES.CANDIDATE_DASHBOARD;
    return <Navigate to={target} replace />;
  }

  return <Outlet />;
}
