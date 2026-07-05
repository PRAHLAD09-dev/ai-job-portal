import { Outlet } from "react-router-dom";
import { DashboardShell } from "@/components/layout/DashboardShell";
import { RECRUITER_NAV } from "@/constants/nav-config";

export function RecruiterLayout() {
  return (
    <DashboardShell items={RECRUITER_NAV}>
      <Outlet />
    </DashboardShell>
  );
}
