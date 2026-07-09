import { useNavigate } from "react-router-dom";
import { ROUTES } from "@/constants/routes";
import { JobForm } from "@/features/recruiter-jobs/components/JobForm";
import { useCreateJob } from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import type { JobFormRequest } from "@/features/recruiter-jobs/types";

export default function CreateJobPage() {
  const navigate = useNavigate();
  const createJob = useCreateJob();

  const handleSubmit = (payload: JobFormRequest) => {
    createJob.mutate(payload, {
      onSuccess: () => navigate(ROUTES.RECRUITER_JOBS),
    });
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Create job</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          New jobs are created as drafts — publish once you're ready for candidates to apply.
        </p>
      </div>
      <JobForm isSubmitting={createJob.isPending} submitLabel="Create job" onSubmit={handleSubmit} />
    </div>
  );
}
