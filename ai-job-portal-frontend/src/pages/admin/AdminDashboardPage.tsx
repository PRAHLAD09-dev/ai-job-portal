import { useEffect } from "react";
import { Briefcase, Building2, FileText, Users } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useAuth } from "@/hooks/useAuth";
import { useAdminDashboard, useRecordAdminLogin } from "@/features/admin/hooks/useAdminDashboard";
import { RecentActivityPanel } from "@/features/admin/components/RecentActivityPanel";
import { DashboardChartsPanel } from "@/features/admin/components/DashboardChartsPanel";

export default function AdminDashboardPage() {
  const { user } = useAuth();
  const { data, isLoading } = useAdminDashboard();
  const recordLogin = useRecordAdminLogin();

  // Record once per admin panel session that this admin logged in (login audit trail).
  useEffect(() => {
    recordLogin.mutate();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const widgets = data
    ? [
        { label: "Total users", icon: Users, value: data.userStatistics.totalUsers },
        { label: "Candidates", icon: Users, value: data.userStatistics.totalCandidates },
        { label: "Recruiters", icon: Users, value: data.userStatistics.totalRecruiters },
        { label: "Companies", icon: Building2, value: data.companyStatistics.totalCompanies },
        { label: "Active jobs", icon: Briefcase, value: data.jobStatistics.publishedJobs },
        { label: "Applications", icon: FileText, value: data.applicationStatistics.totalApplications },
      ]
    : [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Welcome, {user?.firstName}</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Platform overview.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {isLoading
          ? Array.from({ length: 6 }).map((_, i) => (
              <Card key={i}>
                <Skeleton className="h-4 w-24" />
                <Skeleton className="mt-3 h-7 w-16" />
              </Card>
            ))
          : widgets.map(({ label, icon: Icon, value }) => (
              <Card key={label}>
                <div className="flex items-center gap-2">
                  <Icon className="h-4 w-4 text-primary-600" />
                  <p className="text-sm text-[hsl(var(--muted))]">{label}</p>
                </div>
                <p className="mt-2 text-2xl font-semibold">{value}</p>
              </Card>
            ))}
      </div>

      {data && (
        <div>
          <h2 className="text-lg font-semibold tracking-tight">Analytics</h2>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">
            User growth, company verification, jobs, applications, and AI usage.
          </p>
          <div className="mt-4">
            <DashboardChartsPanel />
          </div>
        </div>
      )}

      {data && <RecentActivityPanel activity={data.recentActivity} />}
    </div>
  );
}
