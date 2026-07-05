import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { ROUTES } from "@/constants/routes";
import { PageLoader } from "@/components/common/PageLoader";
import type { RoleName } from "@/types/auth";

/**
 * Restricts a route subtree to specific roles. Every protected route must
 * verify role before rendering; unauthorized users get a 403 page
 * (02_FRONTEND_ARCHITECTURE.md RBAC rules).
 */
export function RoleRoute({ allowed }: { allowed: RoleName[] }) {
  const { user, isInitializing } = useAuth();

  if (isInitializing) return <PageLoader />;
  if (!user) return <Navigate to={ROUTES.LOGIN} replace />;

  const hasAccess = user.roles.some((role) => allowed.includes(role));
  if (!hasAccess) return <Navigate to={ROUTES.FORBIDDEN_403} replace />;

  return <Outlet />;
}
