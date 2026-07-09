import { useNavigate, useParams } from "react-router-dom";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { ROUTES } from "@/constants/routes";
import { JobForm } from "@/features/recruiter-jobs/components/JobForm";
import { useJobPreview, useUpdateJob } from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import type { JobFormRequest } from "@/features/recruiter-jobs/types";

export default function EditJobPage() {
  const { jobId } = useParams<{ jobId: string }>();
  const navigate = useNavigate();
  const { data: job, isLoading } = useJobPreview(jobId);
  const updateJob = useUpdateJob();

  const handleSubmit = (payload: JobFormRequest) => {
    if (!jobId) return;
    updateJob.mutate(
      { jobId, payload },
      { onSuccess: () => navigate(ROUTES.RECRUITER_JOBS) },
    );
  };

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
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Edit job</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">{job.title}</p>
      </div>
      <JobForm job={job} isSubmitting={updateJob.isPending} submitLabel="Save changes" onSubmit={handleSubmit} />
    </div>
  );
}
