import { useMemo } from "react";
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { format, parseISO } from "date-fns";
import { Building2, Briefcase, FileText, Sparkles, TrendingUp } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useAdminDashboardCharts } from "@/features/admin/hooks/useAdminDashboard";
import type { ChartDataPoint } from "@/features/admin/types";

const PIE_COLORS = [
  "var(--color-primary-600)",
  "var(--color-secondary-500)",
  "var(--color-accent-500)",
  "var(--color-warning-500)",
  "var(--color-danger-500)",
  "var(--color-secondary-600)",
];

function EmptyChartState({ message }: { message: string }) {
  return <p className="mt-6 text-center text-sm text-[hsl(var(--muted))]">{message}</p>;
}

function ChartCardSkeleton() {
  return (
    <Card>
      <Skeleton className="h-4 w-40" />
      <Skeleton className="mt-4 h-56 w-full" />
    </Card>
  );
}

function BreakdownBarChart({ data }: { data: ChartDataPoint[] }) {
  if (data.length === 0) return <EmptyChartState message="No data available yet." />;
  return (
    <div className="mt-4 h-56">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" opacity={0.2} />
          <XAxis dataKey="label" tick={{ fontSize: 11 }} interval={0} angle={-15} textAnchor="end" height={45} />
          <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
          <Tooltip />
          <Bar dataKey="value" fill="var(--color-primary-600)" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

function BreakdownPieChart({ data }: { data: ChartDataPoint[] }) {
  const total = data.reduce((sum, d) => sum + d.value, 0);
  if (data.length === 0 || total === 0) return <EmptyChartState message="No data available yet." />;
  return (
    <div className="mt-2 flex h-56 items-center gap-4">
      <ResponsiveContainer width="60%" height="100%">
        <PieChart>
          <Pie data={data} dataKey="value" nameKey="label" innerRadius={45} outerRadius={80} paddingAngle={2}>
            {data.map((entry, index) => (
              <Cell key={entry.label} fill={PIE_COLORS[index % PIE_COLORS.length]} />
            ))}
          </Pie>
          <Tooltip />
        </PieChart>
      </ResponsiveContainer>
      <ul className="flex-1 space-y-1.5 text-xs">
        {data.map((entry, index) => (
          <li key={entry.label} className="flex items-center justify-between gap-2">
            <span className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
              <span
                className="h-2 w-2 shrink-0 rounded-full"
                style={{ backgroundColor: PIE_COLORS[index % PIE_COLORS.length] }}
              />
              {entry.label}
            </span>
            <span className="font-medium">{entry.value}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

/**
 * Admin Dashboard charts (Day 08 "Admin Dashboard: Add Charts") — backed by
 * GET /admin/dashboard/charts. User growth is real signup-timestamp
 * history from Auth Service; the rest are real current-state breakdowns
 * from each owning service's statistics endpoint.
 */
export function DashboardChartsPanel() {
  const { data, isLoading } = useAdminDashboardCharts(30);

  const userGrowthData = useMemo(
    () =>
      (data?.userGrowth ?? []).map((point) => ({
        date: format(parseISO(point.date), "MMM d"),
        signups: point.signupCount,
      })),
    [data?.userGrowth],
  );

  if (isLoading) {
    return (
      <div className="grid gap-4 lg:grid-cols-2">
        <div className="lg:col-span-2">
          <ChartCardSkeleton />
        </div>
        <ChartCardSkeleton />
        <ChartCardSkeleton />
        <ChartCardSkeleton />
      </div>
    );
  }

  if (!data) return null;

  return (
    <div className="grid gap-4 lg:grid-cols-2">
      <Card className="lg:col-span-2">
        <div className="flex items-center gap-2">
          <TrendingUp className="h-4 w-4 text-primary-600" />
          <h3 className="text-base font-semibold">User growth</h3>
        </div>
        <p className="mt-1 text-xs text-[hsl(var(--muted))]">New signups over the last 30 days.</p>
        {userGrowthData.length === 0 ? (
          <EmptyChartState message="No signups recorded yet." />
        ) : (
          <div className="mt-4 h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={userGrowthData}>
                <defs>
                  <linearGradient id="userGrowthFill" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--color-primary-500)" stopOpacity={0.35} />
                    <stop offset="95%" stopColor="var(--color-primary-500)" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" opacity={0.2} />
                <XAxis dataKey="date" tick={{ fontSize: 11 }} minTickGap={20} />
                <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                <Tooltip />
                <Area
                  type="monotone"
                  dataKey="signups"
                  stroke="var(--color-primary-600)"
                  fill="url(#userGrowthFill)"
                  strokeWidth={2}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        )}
      </Card>

      <Card>
        <div className="flex items-center gap-2">
          <Building2 className="h-4 w-4 text-primary-600" />
          <h3 className="text-base font-semibold">Company verification</h3>
        </div>
        <BreakdownPieChart data={data.companyVerification} />
      </Card>

      <Card>
        <div className="flex items-center gap-2">
          <Briefcase className="h-4 w-4 text-primary-600" />
          <h3 className="text-base font-semibold">Jobs by status</h3>
        </div>
        <BreakdownBarChart data={data.jobsByStatus} />
      </Card>

      <Card>
        <div className="flex items-center gap-2">
          <FileText className="h-4 w-4 text-primary-600" />
          <h3 className="text-base font-semibold">Applications by status</h3>
        </div>
        <BreakdownBarChart data={data.applicationsByStatus} />
      </Card>

      <Card>
        <div className="flex items-center gap-2">
          <Sparkles className="h-4 w-4 text-primary-600" />
          <h3 className="text-base font-semibold">AI usage by feature</h3>
        </div>
        <BreakdownPieChart data={data.aiUsageByFeature} />
      </Card>
    </div>
  );
}
