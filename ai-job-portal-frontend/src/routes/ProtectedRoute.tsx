import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { ROUTES } from "@/constants/routes";
import { PageLoader } from "@/components/common/PageLoader";

/** Requires a logged-in user; otherwise redirects to /login (02_FRONTEND_ARCHITECTURE.md RBAC rules). */
export function ProtectedRoute() {
  const { isAuthenticated, isInitializing } = useAuth();
  const location = useLocation();

  if (isInitializing) return <PageLoader />;

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} state={{ from: location }} replace />;
  }

  return <Outlet />;
}
