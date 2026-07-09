import { useState } from "react";
import { Link } from "react-router-dom";
import { Copy, Eye, MoreVertical, Pencil, PlayCircle, StopCircle, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";
import { JobStatusBadge } from "@/features/recruiter-jobs/components/JobStatusBadge";
import { formatEnumLabel } from "@/utils/format";
import { buildRoute } from "@/constants/routes";
import {
  useCloseJob,
  useDeleteJob,
  useDuplicateJob,
  usePublishJob,
  useReopenJob,
} from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import type { JobSummaryResponse } from "@/features/jobs/types";

export function RecruiterJobsTable({ jobs }: { jobs: JobSummaryResponse[] }) {
  const [deleting, setDeleting] = useState<JobSummaryResponse | null>(null);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);

  const publishJob = usePublishJob();
  const closeJob = useCloseJob();
  const reopenJob = useReopenJob();
  const duplicateJob = useDuplicateJob();
  const deleteJob = useDeleteJob();

  return (
    <div className="overflow-x-auto rounded-xl border border-[hsl(var(--border-color))]">
      <table className="w-full text-left text-sm">
        <thead className="sticky top-0 bg-[hsl(var(--surface))] text-xs uppercase text-[hsl(var(--muted))]">
          <tr>
            <th className="px-4 py-3 font-medium">Title</th>
            <th className="px-4 py-3 font-medium">Type</th>
            <th className="px-4 py-3 font-medium">Work mode</th>
            <th className="px-4 py-3 font-medium">Status</th>
            <th className="px-4 py-3 font-medium">Featured</th>
            <th className="px-4 py-3 font-medium text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          {jobs.map((job) => (
            <tr key={job.id} className="border-t border-[hsl(var(--border-color))]">
              <td className="max-w-xs truncate px-4 py-3 font-medium">{job.title}</td>
              <td className="px-4 py-3 text-[hsl(var(--muted))]">{formatEnumLabel(job.jobType)}</td>
              <td className="px-4 py-3 text-[hsl(var(--muted))]">{formatEnumLabel(job.workMode)}</td>
              <td className="px-4 py-3">
                <JobStatusBadge status={job.status} />
              </td>
              <td className="px-4 py-3 text-[hsl(var(--muted))]">{job.featured ? "Yes" : "—"}</td>
              <td className="relative px-4 py-3 text-right">
                <Button
                  variant="ghost"
                  size="sm"
                  aria-label="Job actions"
                  onClick={() => setOpenMenuId(openMenuId === job.id ? null : job.id)}
                >
                  <MoreVertical className="h-4 w-4" />
                </Button>
                {openMenuId === job.id && (
                  <div
                    className="absolute right-4 top-full z-10 mt-1 w-48 rounded-lg border border-[hsl(var(--border-color))] bg-[hsl(var(--surface))] p-1 text-left shadow-lg"
                    onMouseLeave={() => setOpenMenuId(null)}
                  >
                    <Link
                      to={buildRoute.recruiterEditJob(job.id)}
                      className="flex items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
                    >
                      <Pencil className="h-3.5 w-3.5" /> Edit
                    </Link>
                    <Link
                      to={buildRoute.recruiterJobPreview(job.id)}
                      className="flex items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
                    >
                      <Eye className="h-3.5 w-3.5" /> Preview
                    </Link>
                    {job.status === "DRAFT" && (
                      <button
                        type="button"
                        className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
                        onClick={() => {
                          publishJob.mutate(job.id);
                          setOpenMenuId(null);
                        }}
                      >
                        <PlayCircle className="h-3.5 w-3.5" /> Publish
                      </button>
                    )}
                    {job.status === "PUBLISHED" && (
                      <button
                        type="button"
                        className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
                        onClick={() => {
                          closeJob.mutate(job.id);
                          setOpenMenuId(null);
                        }}
                      >
                        <StopCircle className="h-3.5 w-3.5" /> Close
                      </button>
                    )}
                    {(job.status === "CLOSED" || job.status === "ARCHIVED") && (
                      <button
                        type="button"
                        className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
                        onClick={() => {
                          reopenJob.mutate(job.id);
                          setOpenMenuId(null);
                        }}
                      >
                        <PlayCircle className="h-3.5 w-3.5" /> Reopen
                      </button>
                    )}
                    <button
                      type="button"
                      className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
                      onClick={() => {
                        duplicateJob.mutate(job.id);
                        setOpenMenuId(null);
                      }}
                    >
                      <Copy className="h-3.5 w-3.5" /> Duplicate
                    </button>
                    <button
                      type="button"
                      className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm text-danger-500 hover:bg-[hsl(var(--border-color))]/40"
                      onClick={() => {
                        setDeleting(job);
                        setOpenMenuId(null);
                      }}
                    >
                      <Trash2 className="h-3.5 w-3.5" /> Delete
                    </button>
                  </div>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <ConfirmDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(null)}
        title="Delete job"
        description={`Delete "${deleting?.title ?? ""}"? This cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteJob.isPending}
        onConfirm={() => {
          if (deleting) deleteJob.mutate(deleting.id, { onSuccess: () => setDeleting(null) });
        }}
      />
    </div>
  );
}
