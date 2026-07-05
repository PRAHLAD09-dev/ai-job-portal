import { Outlet } from "react-router-dom";
import { DashboardShell } from "@/components/layout/DashboardShell";
import { ADMIN_NAV } from "@/constants/nav-config";

export function AdminLayout() {
  return (
    <DashboardShell items={ADMIN_NAV}>
      <Outlet />
    </DashboardShell>
  );
}
