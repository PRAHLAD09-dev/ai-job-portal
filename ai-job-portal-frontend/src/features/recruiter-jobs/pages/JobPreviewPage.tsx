import { Link, useParams } from "react-router-dom";
import { ArrowLeft, MapPin } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { formatEnumLabel } from "@/utils/format";
import { ROUTES, buildRoute } from "@/constants/routes";
import { useJobPreview } from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import { JobStatusBadge } from "@/features/recruiter-jobs/components/JobStatusBadge";

export default function JobPreviewPage() {
  const { jobId } = useParams<{ jobId: string }>();
  const { data: job, isLoading } = useJobPreview(jobId);

  if (isLoading) {
    return (
      <Card>
        <Skeleton className="h-6 w-48" />
        <Skeleton className="mt-4 h-64 w-full" />
      </Card>
    );
  }

  if (!job) {
    return (
      <Card>
        <p className="text-sm text-[hsl(var(--muted))]">Job not found.</p>
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <Link to={ROUTES.RECRUITER_JOBS} className="inline-flex items-center gap-1 text-sm text-[hsl(var(--muted))] hover:text-[hsl(var(--foreground))]">
          <ArrowLeft className="h-4 w-4" /> Back to jobs
        </Link>
        <Link to={buildRoute.recruiterEditJob(job.id)}>
          <Button size="sm" variant="outline">
            Edit job
          </Button>
        </Link>
      </div>

      <Card>
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 className="text-2xl font-semibold">{job.title}</h1>
            <p className="mt-1 text-sm text-[hsl(var(--muted))]">{job.companyName}</p>
          </div>
          <JobStatusBadge status={job.status} />
        </div>

        <div className="mt-4 flex flex-wrap gap-1.5">
          <Badge variant="outline">{formatEnumLabel(job.jobType)}</Badge>
          <Badge variant="outline">{formatEnumLabel(job.workMode)}</Badge>
          <Badge variant="outline">{formatEnumLabel(job.experienceLevel)}</Badge>
          {job.category && <Badge variant="outline">{job.category.name}</Badge>}
        </div>

        {job.locations.length > 0 && (
          <div className="mt-3 flex items-center gap-1 text-sm text-[hsl(var(--muted))]">
            <MapPin className="h-3.5 w-3.5" />
            {job.locations.map((l) => `${l.city}, ${l.country}`).join(" · ")}
          </div>
        )}

        <div className="mt-6 whitespace-pre-wrap text-sm leading-relaxed">{job.description}</div>

        {job.skills.length > 0 && (
          <div className="mt-6">
            <p className="text-sm font-medium">Required skills</p>
            <div className="mt-2 flex flex-wrap gap-1.5">
              {job.skills.map((s) => (
                <Badge key={s.id} variant={s.mandatory ? "primary" : "default"}>
                  {s.name}
                </Badge>
              ))}
            </div>
          </div>
        )}

        {job.requirements.length > 0 && (
          <div className="mt-6">
            <p className="text-sm font-medium">Requirements &amp; responsibilities</p>
            <ul className="mt-2 list-inside list-disc space-y-1 text-sm text-[hsl(var(--muted))]">
              {job.requirements
                .slice()
                .sort((a, b) => a.displayOrder - b.displayOrder)
                .map((r) => (
                  <li key={r.id}>{r.description}</li>
                ))}
            </ul>
          </div>
        )}

        {job.benefits.length > 0 && (
          <div className="mt-6">
            <p className="text-sm font-medium">Benefits</p>
            <ul className="mt-2 list-inside list-disc space-y-1 text-sm text-[hsl(var(--muted))]">
              {job.benefits.map((b) => (
                <li key={b.id}>{b.title}</li>
              ))}
            </ul>
          </div>
        )}
      </Card>
    </div>
  );
}
