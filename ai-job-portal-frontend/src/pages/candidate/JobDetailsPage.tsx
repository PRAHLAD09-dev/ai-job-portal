import { useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { toast } from "sonner";
import {
  Bookmark,
  BriefcaseBusiness,
  Building2,
  Calendar,
  CheckCircle2,
  Clock,
  ExternalLink,
  MapPin,
  Share2,
  Sparkles,
  Wallet,
  Zap,
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button, buttonVariants } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { formatEnumLabel } from "@/utils/format";
import { buildRoute } from "@/constants/routes";
import { useJobDetails, useSimilarJobs } from "@/features/jobs/hooks/useJobs";
import { useSaveJob, useSavedJobIds, useUnsaveJob } from "@/features/jobs/hooks/useSavedJobs";
import { JobCard } from "@/features/jobs/components/JobCard";
import { ApplyJobDialog } from "@/features/applications/components/ApplyJobDialog";
import { useApplicationForJob } from "@/features/applications/hooks/useApplications";
import { useGenerateJobMatch, useJobMatch } from "@/features/ai/hooks/useAi";
import { MatchBreakdownBars } from "@/features/ai/components/MatchBreakdownBars";
import { isNotFoundError } from "@/services/api-client";
import type { JobResponse } from "@/features/jobs/types";

function formatSalaryRange(job: JobResponse): string | null {
  if (job.minSalary == null && job.maxSalary == null) return null;
  const currency = job.currency ?? "";
  const period = job.salaryType ? `/${formatEnumLabel(job.salaryType).toLowerCase()}` : "";
  if (job.minSalary != null && job.maxSalary != null) {
    return `${currency} ${job.minSalary.toLocaleString()} - ${job.maxSalary.toLocaleString()}${period}`;
  }
  return `${currency} ${(job.minSalary ?? job.maxSalary)?.toLocaleString()}${period}`;
}

export default function JobDetailsPage() {
  const { jobId } = useParams<{ jobId: string }>();
  const { data: job, isLoading } = useJobDetails(jobId);
  const { data: similarJobs } = useSimilarJobs(jobId);
  const savedJobIds = useSavedJobIds();
  const saveJob = useSaveJob();
  const unsaveJob = useUnsaveJob();
  const [isApplyOpen, setIsApplyOpen] = useState(false);
  const { application: existingApplication, isApplied } = useApplicationForJob(jobId);
  const { data: jobMatch, isLoading: isMatchLoading, error: jobMatchError } = useJobMatch(jobId);
  const generateJobMatch = useGenerateJobMatch(jobId);

  // Hooks must run unconditionally on every render (Rules of Hooks), so
  // these are computed before the isLoading/!job early returns below,
  // guarding internally for a not-yet-loaded job.
  const qualifications = useMemo(
    () =>
      (job?.requirements ?? [])
        .filter((r) => r.type === "QUALIFICATION")
        .sort((a, b) => a.displayOrder - b.displayOrder),
    [job?.requirements],
  );
  const responsibilities = useMemo(
    () =>
      (job?.requirements ?? [])
        .filter((r) => r.type === "RESPONSIBILITY")
        .sort((a, b) => a.displayOrder - b.displayOrder),
    [job?.requirements],
  );
  const niceToHaves = useMemo(
    () =>
      (job?.requirements ?? [])
        .filter((r) => r.type === "NICE_TO_HAVE")
        .sort((a, b) => a.displayOrder - b.displayOrder),
    [job?.requirements],
  );

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-64" />
        <Skeleton className="h-40 w-full" />
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  if (!job) {
    return (
      <EmptyState
        icon={<BriefcaseBusiness className="h-8 w-8" />}
        title="Job not found"
        message="This job may have been removed or is no longer available."
      />
    );
  }

  const isSaved = savedJobIds.has(job.id);
  const salary = formatSalaryRange(job);

  const handleShare = async () => {
    try {
      await navigator.clipboard.writeText(window.location.href);
      toast.success("Job link copied to clipboard");
    } catch {
      toast.error("Could not copy link");
    }
  };

  return (
    <div className="space-y-6">
      <Card>
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
          <div className="flex items-start gap-4">
            <div className="flex h-14 w-14 shrink-0 items-center justify-center overflow-hidden rounded-xl border border-[hsl(var(--border-color))] bg-[hsl(var(--background))]">
              {job.companyLogoUrl ? (
                <img src={job.companyLogoUrl} alt={job.companyName} className="h-full w-full object-cover" />
              ) : (
                <Building2 className="h-7 w-7 text-[hsl(var(--muted))]" />
              )}
            </div>
            <div>
              <h1 className="text-xl font-semibold">{job.title}</h1>
              <p className="text-sm text-[hsl(var(--muted))]">{job.companyName}</p>
              <div className="mt-2 flex flex-wrap gap-1.5">
                {job.featured && <Badge variant="primary">Featured</Badge>}
                <Badge variant="outline">{formatEnumLabel(job.jobType)}</Badge>
                <Badge variant="outline">{formatEnumLabel(job.workMode)}</Badge>
                <Badge variant="outline">{formatEnumLabel(job.experienceLevel)}</Badge>
                {job.applyMethod === "QUICK_APPLY" && (
                  <Badge variant="success">
                    <Zap className="h-3 w-3" /> Quick Apply
                  </Badge>
                )}
                {job.applyMethod === "EXTERNAL_APPLY" && (
                  <Badge variant="outline">
                    <ExternalLink className="h-3 w-3" /> External Apply
                  </Badge>
                )}
              </div>
            </div>
          </div>

          <div className="flex shrink-0 gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => (isSaved ? unsaveJob.mutate(job.id) : saveJob.mutate(job.id))}
              disabled={saveJob.isPending || unsaveJob.isPending}
            >
              <Bookmark className={`h-4 w-4 ${isSaved ? "fill-primary-600 text-primary-600" : ""}`} />
              {isSaved ? "Saved" : "Save"}
            </Button>
            <Button variant="outline" size="sm" onClick={handleShare}>
              <Share2 className="h-4 w-4" /> Share
            </Button>
            {job.applyMethod === "EXTERNAL_APPLY" ? (
              <Button
                size="sm"
                disabled={job.status !== "PUBLISHED" || !job.externalApplyUrl}
                onClick={() => window.open(job.externalApplyUrl ?? "", "_blank", "noopener,noreferrer")}
              >
                <ExternalLink className="h-4 w-4" /> Apply on Company Site
              </Button>
            ) : isApplied && existingApplication ? (
              <Link
                to={buildRoute.candidateApplicationDetails(existingApplication.id)}
                className={buttonVariants({ variant: "outline", size: "sm" })}
              >
                <CheckCircle2 className="h-4 w-4 text-primary-600" /> Already Applied
              </Link>
            ) : (
              <Button size="sm" onClick={() => setIsApplyOpen(true)} disabled={job.status !== "PUBLISHED"}>
                {job.applyMethod === "QUICK_APPLY" && <Zap className="h-4 w-4" />}
                {job.applyMethod === "QUICK_APPLY" ? "Quick Apply" : "Apply Now"}
              </Button>
            )}
          </div>
        </div>

        <div className="mt-6 grid grid-cols-2 gap-4 border-t border-[hsl(var(--border-color))] pt-4 text-sm sm:grid-cols-4">
          <div>
            <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
              <MapPin className="h-4 w-4" /> Location
            </p>
            <p className="mt-1 font-medium">
              {job.locations.length > 0
                ? job.locations.map((l) => `${l.city}, ${l.country}`).join(" · ")
                : formatEnumLabel(job.workMode)}
            </p>
          </div>
          {salary && (
            <div>
              <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
                <Wallet className="h-4 w-4" /> Salary
              </p>
              <p className="mt-1 font-medium">{salary}</p>
            </div>
          )}
          <div>
            <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
              <Clock className="h-4 w-4" /> Vacancies
            </p>
            <p className="mt-1 font-medium">{job.vacancies}</p>
          </div>
          {job.applicationDeadline && (
            <div>
              <p className="flex items-center gap-1.5 text-[hsl(var(--muted))]">
                <Calendar className="h-4 w-4" /> Apply Before
              </p>
              <p className="mt-1 font-medium">{new Date(job.applicationDeadline).toLocaleDateString()}</p>
            </div>
          )}
        </div>
      </Card>

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        <div className="space-y-6">
          <Card>
            <h2 className="text-base font-semibold">Job Description</h2>
            <p className="mt-3 whitespace-pre-line text-sm leading-relaxed">{job.description}</p>
          </Card>

          {responsibilities.length > 0 && (
            <Card>
              <h2 className="text-base font-semibold">Responsibilities</h2>
              <ul className="mt-3 space-y-2">
                {responsibilities.map((r) => (
                  <li key={r.id} className="flex gap-2 text-sm">
                    <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-primary-600" /> {r.description}
                  </li>
                ))}
              </ul>
            </Card>
          )}

          {qualifications.length > 0 && (
            <Card>
              <h2 className="text-base font-semibold">Qualifications</h2>
              <ul className="mt-3 space-y-2">
                {qualifications.map((r) => (
                  <li key={r.id} className="flex gap-2 text-sm">
                    <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-primary-600" /> {r.description}
                  </li>
                ))}
              </ul>
            </Card>
          )}

          {niceToHaves.length > 0 && (
            <Card>
              <h2 className="text-base font-semibold">Nice to Have</h2>
              <ul className="mt-3 space-y-2">
                {niceToHaves.map((r) => (
                  <li key={r.id} className="flex gap-2 text-sm">
                    <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-[hsl(var(--muted))]" /> {r.description}
                  </li>
                ))}
              </ul>
            </Card>
          )}

          {job.benefits.length > 0 && (
            <Card>
              <h2 className="text-base font-semibold">Benefits</h2>
              <div className="mt-3 grid gap-3 sm:grid-cols-2">
                {job.benefits.map((benefit) => (
                  <div key={benefit.id} className="rounded-lg border border-[hsl(var(--border-color))] p-3">
                    <p className="text-sm font-medium">{benefit.title}</p>
                    {benefit.description && (
                      <p className="mt-1 text-xs text-[hsl(var(--muted))]">{benefit.description}</p>
                    )}
                  </div>
                ))}
              </div>
            </Card>
          )}
        </div>

        <div className="space-y-6">
          <Card>
            <div className="flex items-center justify-between">
              <h2 className="flex items-center gap-1.5 text-base font-semibold">
                <Sparkles className="h-4 w-4 text-primary-600" /> AI Match Score
              </h2>
              {jobMatch && <span className="text-lg font-semibold text-primary-600">{jobMatch.matchScore}%</span>}
            </div>

            {isMatchLoading ? (
              <div className="mt-3 space-y-2">
                <Skeleton className="h-3 w-full" />
                <Skeleton className="h-3 w-full" />
                <Skeleton className="h-3 w-full" />
              </div>
            ) : jobMatch ? (
              <div className="mt-3 space-y-3">
                <MatchBreakdownBars breakdown={jobMatch.matchBreakdown} />
                {jobMatch.reasoning.length > 0 && (
                  <ul className="space-y-1.5 border-t border-[hsl(var(--border-color))] pt-3">
                    {jobMatch.reasoning.map((point, index) => (
                      <li key={index} className="flex gap-2 text-xs text-[hsl(var(--muted))]">
                        <CheckCircle2 className="mt-0.5 h-3.5 w-3.5 shrink-0 text-primary-600" /> {point}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            ) : isNotFoundError(jobMatchError) ? (
              <div className="mt-3 space-y-2">
                <p className="text-sm text-[hsl(var(--muted))]">
                  Run AI Match to see how well your profile fits this job.
                </p>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => generateJobMatch.mutate()}
                  isLoading={generateJobMatch.isPending}
                >
                  <Sparkles className="h-4 w-4" /> Generate My Match Score
                </Button>
              </div>
            ) : (
              <p className="mt-3 text-sm text-[hsl(var(--muted))]">Match score is unavailable right now.</p>
            )}
          </Card>

          {job.skills.length > 0 && (
            <Card>
              <h2 className="text-base font-semibold">Skills</h2>
              <div className="mt-3 flex flex-wrap gap-2">
                {job.skills.map((skill) => (
                  <Badge key={skill.id} variant={skill.mandatory ? "primary" : "outline"}>
                    {skill.name}
                  </Badge>
                ))}
              </div>
            </Card>
          )}

          {similarJobs && similarJobs.length > 0 && (
            <div>
              <h2 className="mb-3 text-base font-semibold">Similar Jobs</h2>
              <div className="space-y-3">
                {similarJobs.map((similarJob) => (
                  <JobCard key={similarJob.id} job={similarJob} />
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {job.applyMethod !== "EXTERNAL_APPLY" && (
        <ApplyJobDialog
          open={isApplyOpen}
          onOpenChange={setIsApplyOpen}
          jobId={job.id}
          jobTitle={job.title}
          applyMethod={job.applyMethod}
        />
      )}
    </div>
  );
}
