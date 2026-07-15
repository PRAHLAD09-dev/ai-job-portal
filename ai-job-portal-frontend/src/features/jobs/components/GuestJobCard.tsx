import { memo } from "react";
import { Link } from "react-router-dom";
import { BriefcaseBusiness, MapPin, Sparkles } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { buildRoute } from "@/constants/routes";
import { formatEnumLabel } from "@/utils/format";
import type { JobSummaryResponse } from "@/features/jobs/types";

function formatSalary(job: JobSummaryResponse): string | null {
  if (job.minSalary == null && job.maxSalary == null) return null;
  const currency = job.currency ?? "";
  const period = job.salaryType ? `/${formatEnumLabel(job.salaryType).toLowerCase()}` : "";
  if (job.minSalary != null && job.maxSalary != null) {
    return `${currency} ${job.minSalary.toLocaleString()} - ${job.maxSalary.toLocaleString()}${period}`;
  }
  const value = job.minSalary ?? job.maxSalary;
  return `${currency} ${value?.toLocaleString()}${period}`;
}

/**
 * Public Jobs page card. Deliberately a separate component from
 * {@code JobCard} rather than a shared one with a "hideSaveButton"
 * flag: {@code JobCard} calls {@code useSavedJobIds}/{@code useSaveJob}
 * on every render, which hit an authenticated endpoint and would 401
 * for a signed-out visitor. This card renders the same summary info
 * without touching any authenticated data.
 *
 * Clicking through goes to the existing job details route, which is
 * behind the candidate ProtectedRoute — a signed-out visitor is
 * redirected to Login, matching the "browse public, sign in for full
 * detail/apply" pattern common to job boards. No new route was added.
 */
function GuestJobCardComponent({ job }: { job: JobSummaryResponse }) {
  const salary = formatSalary(job);

  return (
    <Link to={buildRoute.candidateJobDetails(job.id)}>
      <Card className="transition-shadow hover:shadow-md">
        <div className="flex items-start gap-3">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-lg border border-[hsl(var(--border-color))] bg-[hsl(var(--background))]">
            {job.companyLogoUrl ? (
              <img src={job.companyLogoUrl} alt={job.companyName} className="h-full w-full object-cover" />
            ) : (
              <BriefcaseBusiness className="h-5 w-5 text-[hsl(var(--muted))]" />
            )}
          </div>
          <div className="min-w-0">
            <p className="truncate font-semibold">{job.title}</p>
            <p className="truncate text-sm text-[hsl(var(--muted))]">{job.companyName}</p>
          </div>
        </div>

        <div className="mt-3 flex flex-wrap gap-1.5">
          {job.featured && (
            <Badge variant="primary">
              <Sparkles className="h-3 w-3" /> Featured
            </Badge>
          )}
          <Badge variant="outline">{formatEnumLabel(job.jobType)}</Badge>
          <Badge variant="outline">{formatEnumLabel(job.workMode)}</Badge>
          <Badge variant="outline">{formatEnumLabel(job.experienceLevel)}</Badge>
        </div>

        <div className="mt-3 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-[hsl(var(--muted))]">
          {job.cities.length > 0 && (
            <span className="flex items-center gap-1">
              <MapPin className="h-3.5 w-3.5" /> {job.cities.join(", ")}
            </span>
          )}
          {salary && <span>{salary}</span>}
        </div>
      </Card>
    </Link>
  );
}

export const GuestJobCard = memo(GuestJobCardComponent);
