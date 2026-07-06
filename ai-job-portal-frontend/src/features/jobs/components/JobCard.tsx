import { memo, type MouseEvent } from "react";
import { Link } from "react-router-dom";
import { Bookmark, BriefcaseBusiness, MapPin, Sparkles } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { buildRoute } from "@/constants/routes";
import { formatEnumLabel } from "@/utils/format";
import { useSaveJob, useSavedJobIds, useUnsaveJob } from "@/features/jobs/hooks/useSavedJobs";
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

function JobCardComponent({ job }: { job: JobSummaryResponse }) {
  const savedJobIds = useSavedJobIds();
  const saveJob = useSaveJob();
  const unsaveJob = useUnsaveJob();
  const isSaved = savedJobIds.has(job.id);
  const isToggling = saveJob.isPending || unsaveJob.isPending;

  const toggleSave = (event: MouseEvent) => {
    event.preventDefault();
    event.stopPropagation();
    if (isSaved) {
      unsaveJob.mutate(job.id);
    } else {
      saveJob.mutate(job.id);
    }
  };

  const salary = formatSalary(job);

  return (
    <Link to={buildRoute.candidateJobDetails(job.id)}>
      <Card className="transition-shadow hover:shadow-md">
        <div className="flex items-start justify-between gap-3">
          <div className="flex min-w-0 items-start gap-3">
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
          <button
            type="button"
            aria-label={isSaved ? "Remove from saved jobs" : "Save job"}
            onClick={toggleSave}
            disabled={isToggling}
            className="shrink-0 rounded-md p-1.5 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40 disabled:opacity-50"
          >
            <Bookmark className={`h-5 w-5 ${isSaved ? "fill-primary-600 text-primary-600" : ""}`} />
          </button>
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

/** Memoized: rendered in lists of up to 12+ items; skips re-render when the parent re-renders (e.g. search input keystrokes) but this card's `job` prop is unchanged. */
export const JobCard = memo(JobCardComponent);
