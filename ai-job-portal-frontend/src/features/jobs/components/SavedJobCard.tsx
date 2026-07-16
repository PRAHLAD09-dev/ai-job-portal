import { Link } from "react-router-dom";
import { Building2, Calendar, MapPin, Sparkles, X } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { buildRoute } from "@/constants/routes";
import { formatEnumLabel } from "@/utils/format";
import type { SavedJobResponse } from "@/features/jobs/types";

function formatSalary(job: SavedJobResponse["job"]): string | null {
  if (job.minSalary == null && job.maxSalary == null) return null;
  const currency = job.currency ?? "";
  const period = job.salaryType ? `/${formatEnumLabel(job.salaryType).toLowerCase()}` : "";
  if (job.minSalary != null && job.maxSalary != null) {
    return `${currency} ${job.minSalary.toLocaleString()} - ${job.maxSalary.toLocaleString()}${period}`;
  }
  const value = job.minSalary ?? job.maxSalary;
  return `${currency} ${value?.toLocaleString()}${period}`;
}

interface SavedJobCardProps {
  savedJob: SavedJobResponse;
  /** AI job-recommendation match score for this job, if the candidate has one — otherwise omitted, never guessed. */
  matchScore?: number;
  onRemove: (jobId: string) => void;
  isRemoving?: boolean;
}

/** DAY07 "Saved Job Card": shared component used on the Saved Jobs page. */
export function SavedJobCard({ savedJob, matchScore, onRemove, isRemoving }: SavedJobCardProps) {
  const { job } = savedJob;

  return (
    <Card className="flex flex-col gap-3">
      <div className="flex items-start justify-between gap-3">
        <Link to={buildRoute.candidateJobDetails(job.id)} className="min-w-0">
          <p className="truncate font-semibold hover:underline">{job.title}</p>
          <p className="mt-1 flex items-center gap-1 text-sm text-[hsl(var(--muted))]">
            <Building2 className="h-3.5 w-3.5" /> {job.companyName}
          </p>
        </Link>
        {matchScore != null && (
          <Badge variant="success" className="shrink-0">
            <Sparkles className="h-3 w-3" /> {Math.round(matchScore)}% match
          </Badge>
        )}
      </div>

      <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-[hsl(var(--muted))]">
        {job.cities.length > 0 && (
          <span className="flex items-center gap-1">
            <MapPin className="h-3.5 w-3.5" /> {job.cities.join(", ")}
          </span>
        )}
        <span className="flex items-center gap-1">
          <Calendar className="h-3.5 w-3.5" /> Saved {new Date(savedJob.savedAt).toLocaleDateString()}
        </span>
      </div>

      {formatSalary(job) && <p className="text-sm font-medium text-secondary-600">{formatSalary(job)}</p>}

      <div className="mt-1 flex items-center justify-between gap-2">
        <Link to={buildRoute.candidateJobDetails(job.id)}>
          <Button size="sm" variant="outline">
            View job
          </Button>
        </Link>
        <Button
          size="sm"
          variant="ghost"
          onClick={() => onRemove(job.id)}
          isLoading={isRemoving}
          aria-label="Remove saved job"
        >
          <X className="h-4 w-4" /> Remove
        </Button>
      </div>
    </Card>
  );
}
