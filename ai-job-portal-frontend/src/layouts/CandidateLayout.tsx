import { Outlet } from "react-router-dom";
import { DashboardShell } from "@/components/layout/DashboardShell";
import { CANDIDATE_NAV } from "@/constants/nav-config";

export function CandidateLayout() {
  return (
    <DashboardShell items={CANDIDATE_NAV}>
      <Outlet />
    </DashboardShell>
  );
}
