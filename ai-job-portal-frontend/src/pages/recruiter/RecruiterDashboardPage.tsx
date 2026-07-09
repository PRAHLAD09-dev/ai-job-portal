import { Link } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { Briefcase, Building2, Sparkles, Users } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";
import { formatEnumLabel } from "@/utils/format";
import { ROUTES } from "@/constants/routes";
import { useMyCompany } from "@/features/recruiter-company/hooks/useCompany";
import { useRecruiterJobStatistics } from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import { useRecruiterApplicationStatistics } from "@/features/recruiter-applications/hooks/useRecruiterApplications";
import { useLatestNotifications } from "@/features/notifications/hooks/useNotifications";

export default function RecruiterDashboardPage() {
  const { user } = useAuth();
  const { data: company, isLoading: companyLoading } = useMyCompany();
  const { data: jobStats } = useRecruiterJobStatistics();
  const { data: applicationStats } = useRecruiterApplicationStatistics();
  const { data: notifications, isLoading: notificationsLoading } = useLatestNotifications(true);

  const chartData = applicationStats
    ? Object.entries(applicationStats.countByStatus).map(([status, count]) => ({
        status: formatEnumLabel(status),
        count,
      }))
    : [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Welcome back, {user?.firstName}</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Here's your hiring overview.</p>
      </div>

      <Card>
        <div className="flex items-center gap-2">
          <Building2 className="h-5 w-5 text-primary-600" />
          <h2 className="text-base font-semibold">Company overview</h2>
        </div>
        {companyLoading ? (
          <Skeleton className="mt-4 h-16 w-full" />
        ) : company ? (
          <div className="mt-4 flex flex-wrap items-center gap-4">
            {company.logoUrl && <img src={company.logoUrl} alt={company.name} className="h-12 w-12 rounded-lg object-cover" />}
            <div>
              <p className="font-medium">{company.name}</p>
              <p className="text-sm text-[hsl(var(--muted))]">{formatEnumLabel(company.industry)}</p>
            </div>
            <Badge variant={company.verificationStatus === "VERIFIED" ? "success" : "warning"}>
              {formatEnumLabel(company.verificationStatus)}
            </Badge>
            <Link to={ROUTES.RECRUITER_COMPANY} className="ml-auto text-sm text-primary-600 hover:underline">
              Manage company
            </Link>
          </div>
        ) : (
          <div className="mt-4">
            <p className="text-sm text-[hsl(var(--muted))]">No data available.</p>
            <Link to={ROUTES.RECRUITER_COMPANY}>
              <Button size="sm" className="mt-3">
                Register your company
              </Button>
            </Link>
          </div>
        )}
      </Card>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card>
          <p className="text-sm text-[hsl(var(--muted))]">Active jobs</p>
          <p className="mt-2 text-2xl font-semibold">{jobStats?.activeJobs ?? "—"}</p>
        </Card>
        <Card>
          <p className="text-sm text-[hsl(var(--muted))]">Total applications</p>
          <p className="mt-2 text-2xl font-semibold">{applicationStats?.totalApplications ?? "—"}</p>
        </Card>
        <Card>
          <p className="text-sm text-[hsl(var(--muted))]">Shortlisted</p>
          <p className="mt-2 text-2xl font-semibold">{applicationStats?.countByStatus?.SHORTLISTED ?? 0}</p>
        </Card>
        <Card>
          <p className="text-sm text-[hsl(var(--muted))]">Interviews scheduled</p>
          <p className="mt-2 text-2xl font-semibold">{applicationStats?.countByStatus?.INTERVIEW ?? 0}</p>
        </Card>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <div className="flex items-center gap-2">
            <Briefcase className="h-5 w-5 text-primary-600" />
            <h2 className="text-base font-semibold">Hiring statistics</h2>
          </div>
          {chartData.length === 0 ? (
            <p className="mt-6 text-sm text-[hsl(var(--muted))]">No data available.</p>
          ) : (
            <div className="mt-4 h-72">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" opacity={0.2} />
                  <XAxis dataKey="status" tick={{ fontSize: 12 }} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                  <Tooltip />
                  <Bar dataKey="count" fill="var(--color-primary-600)" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </Card>

        <Card>
          <div className="flex items-center gap-2">
            <Users className="h-5 w-5 text-primary-600" />
            <h2 className="text-base font-semibold">Recent notifications</h2>
          </div>
          <div className="mt-4 space-y-3">
            {notificationsLoading && <Skeleton className="h-16 w-full" />}
            {!notificationsLoading && (!notifications || notifications.length === 0) && (
              <p className="text-sm text-[hsl(var(--muted))]">No data available.</p>
            )}
            {notifications?.slice(0, 5).map((n) => (
              <div key={n.id} className="text-sm">
                <p className="font-medium">{n.title}</p>
                <p className="text-xs text-[hsl(var(--muted))]">{n.message}</p>
              </div>
            ))}
          </div>
        </Card>
      </div>

      <Card>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-primary-600" />
            <div>
              <h2 className="text-base font-semibold">AI insights</h2>
              <p className="text-sm text-[hsl(var(--muted))]">
                Generate job descriptions, interview questions, and find your best-matched candidates.
              </p>
            </div>
          </div>
          <Link to={ROUTES.RECRUITER_AI}>
            <Button variant="outline" size="sm">
              Open AI tools
            </Button>
          </Link>
        </div>
      </Card>
    </div>
  );
}
