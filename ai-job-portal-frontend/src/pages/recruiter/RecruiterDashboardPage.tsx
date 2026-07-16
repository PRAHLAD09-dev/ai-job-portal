import { Link } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { Bookmark, Briefcase, Building2, Eye, EyeOff, Sparkles, Users } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { EmptyState } from "@/components/common/EmptyState";
import { useAuth } from "@/hooks/useAuth";
import { formatEnumLabel } from "@/utils/format";
import { ROUTES } from "@/constants/routes";
import { useMyCompany } from "@/features/recruiter-company/hooks/useCompany";
import { useLatestNotifications } from "@/features/notifications/hooks/useNotifications";
import { useRecruiterDashboard } from "@/features/recruiter-dashboard/hooks/useRecruiterDashboard";

export default function RecruiterDashboardPage() {
  const { user } = useAuth();
  const { data: company, isLoading: companyLoading } = useMyCompany();
  const { data: dashboard, isLoading: isDashboardLoading } = useRecruiterDashboard();
  const { data: notifications, isLoading: notificationsLoading } = useLatestNotifications(true);

  const jobStats = dashboard?.jobStatistics;
  const applicationStats = dashboard?.applicationStatistics;

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
          {isDashboardLoading ? <Skeleton className="mt-2 h-8 w-16" /> : <p className="mt-2 text-2xl font-semibold">{jobStats?.activeJobs ?? "—"}</p>}
        </Card>
        <Card>
          <p className="text-sm text-[hsl(var(--muted))]">Total applications</p>
          {isDashboardLoading ? (
            <Skeleton className="mt-2 h-8 w-16" />
          ) : (
            <p className="mt-2 text-2xl font-semibold">{applicationStats?.totalApplications ?? "—"}</p>
          )}
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
            <Users className="h-5 w-5 text-primary-600" />
            <h2 className="text-base font-semibold">Recent applications</h2>
          </div>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">AI Match score and recruiter-viewed status at a glance.</p>
          {isDashboardLoading && <Skeleton className="mt-4 h-48 w-full" />}
          {!isDashboardLoading && (!dashboard?.recentApplications || dashboard.recentApplications.length === 0) && (
            <p className="mt-6 text-sm text-[hsl(var(--muted))]">No applications yet.</p>
          )}
          {!isDashboardLoading && dashboard && dashboard.recentApplications.length > 0 && (
            <div className="mt-4 overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="border-b border-[hsl(var(--border-color))] text-xs text-[hsl(var(--muted))]">
                  <tr>
                    <th className="py-2 pr-4 font-medium">Candidate</th>
                    <th className="py-2 pr-4 font-medium">Job</th>
                    <th className="py-2 pr-4 font-medium">Status</th>
                    <th className="py-2 pr-4 font-medium">AI Match</th>
                    <th className="py-2 pr-4 font-medium">Viewed</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[hsl(var(--border-color))]">
                  {dashboard.recentApplications.map((row) => (
                    <tr key={row.applicationId}>
                      <td className="py-2 pr-4">{row.candidateName}</td>
                      <td className="py-2 pr-4 text-[hsl(var(--muted))]">{row.jobTitle}</td>
                      <td className="py-2 pr-4">
                        <Badge variant="outline">{formatEnumLabel(row.status)}</Badge>
                      </td>
                      <td className="py-2 pr-4">
                        {row.aiMatchScore != null ? (
                          <span className="flex items-center gap-1 font-medium text-secondary-600">
                            <Sparkles className="h-3.5 w-3.5" /> {row.aiMatchScore}%
                          </span>
                        ) : (
                          <span className="text-[hsl(var(--muted))]">Not analyzed</span>
                        )}
                      </td>
                      <td className="py-2 pr-4">
                        {row.viewed ? (
                          <span className="flex items-center gap-1 text-success-500">
                            <Eye className="h-3.5 w-3.5" /> Viewed
                          </span>
                        ) : (
                          <span className="flex items-center gap-1 text-[hsl(var(--muted))]">
                            <EyeOff className="h-3.5 w-3.5" /> New
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>

        <Card>
          <div className="flex items-center gap-2">
            <Bookmark className="h-5 w-5 text-primary-600" />
            <h2 className="text-base font-semibold">Saved job statistics</h2>
          </div>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">How many candidates have bookmarked each job.</p>
          {isDashboardLoading && <Skeleton className="mt-4 h-40 w-full" />}
          {!isDashboardLoading && (!dashboard?.savedJobStatistics || dashboard.savedJobStatistics.length === 0) && (
            <EmptyState
              icon={<Bookmark className="h-6 w-6" />}
              title="No saved jobs yet"
              message="Once candidates bookmark your jobs, they'll show up here."
            />
          )}
          {!isDashboardLoading && dashboard && dashboard.savedJobStatistics.length > 0 && (
            <ul className="mt-4 space-y-3">
              {dashboard.savedJobStatistics
                .slice()
                .sort((a, b) => b.savedCount - a.savedCount)
                .slice(0, 6)
                .map((row) => (
                  <li key={row.jobId} className="flex items-center justify-between gap-3 text-sm">
                    <span className="truncate">{row.jobTitle}</span>
                    <Badge variant="primary">{row.savedCount}</Badge>
                  </li>
                ))}
            </ul>
          )}
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
