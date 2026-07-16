import { useQuery } from "@tanstack/react-query";
import { recruiterDashboardService } from "@/features/recruiter-dashboard/services/recruiter-dashboard.service";

/** DAY11 "Recruiter Dashboard Improvements" — AI Match, Viewed Status, Saved Job Statistics. */
export function useRecruiterDashboard() {
  return useQuery({
    queryKey: ["recruiter", "dashboard"],
    queryFn: () => recruiterDashboardService.getDashboard().then((res) => res.data),
  });
}
