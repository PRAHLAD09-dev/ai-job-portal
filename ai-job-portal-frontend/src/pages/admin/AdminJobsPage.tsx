import { useState } from "react";
import { Archive, Briefcase, RotateCcw, Search, Star, StarOff } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { useDebouncedValue } from "@/hooks/useDebouncedValue";
import { formatEnumLabel } from "@/utils/format";
import { AdminStatusBadge } from "@/features/admin/components/AdminStatusBadge";
import { useAdminJobs, useFeatureJob, useRemoveJob, useRestoreJob, useUnfeatureJob } from "@/features/admin/hooks/useAdminJobs";

const STATUS_OPTIONS = ["ALL", "DRAFT", "PUBLISHED", "CLOSED", "ARCHIVED"];
const PAGE_SIZE = 10;

export default function AdminJobsPage() {
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState("ALL");
  const debouncedKeyword = useDebouncedValue(keyword, 300);

  const { data, isLoading, isFetching } = useAdminJobs({
    page,
    size: PAGE_SIZE,
    keyword: debouncedKeyword || undefined,
    status: status === "ALL" ? undefined : status,
  });

  const removeJob = useRemoveJob();
  const restoreJob = useRestoreJob();
  const featureJob = useFeatureJob();
  const unfeatureJob = useUnfeatureJob();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Jobs</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Moderate every job posted across the platform.</p>
      </div>

      <Card>
        <div className="flex flex-col gap-3 sm:flex-row">
          <div className="relative flex-1">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
            <Input
              className="pl-9"
              placeholder="Search jobs by title..."
              value={keyword}
              onChange={(e) => {
                setKeyword(e.target.value);
                setPage(0);
              }}
            />
          </div>
          <Select
            className="sm:w-48"
            value={status}
            onChange={(e) => {
              setStatus(e.target.value);
              setPage(0);
            }}
          >
            {STATUS_OPTIONS.map((s) => (
              <option key={s} value={s}>
                {s === "ALL" ? "All statuses" : formatEnumLabel(s)}
              </option>
            ))}
          </Select>
        </div>
      </Card>

      {isLoading ? (
        <div className="space-y-2">
          <Skeleton className="h-14 w-full" />
          <Skeleton className="h-14 w-full" />
          <Skeleton className="h-14 w-full" />
        </div>
      ) : !data || data.content.length === 0 ? (
        <EmptyState icon={<Briefcase className="h-10 w-10" />} title="No records found" message="No jobs match your current search or filters." />
      ) : (
        <div className="overflow-x-auto rounded-xl border border-[hsl(var(--border-color))]">
          <table className="w-full text-left text-sm">
            <thead className="bg-[hsl(var(--surface))] text-xs uppercase text-[hsl(var(--muted))]">
              <tr>
                <th className="px-4 py-3 font-medium">Title</th>
                <th className="px-4 py-3 font-medium">Company</th>
                <th className="px-4 py-3 font-medium">Type</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium">Featured</th>
                <th className="px-4 py-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((job) => (
                <tr key={job.id} className="border-t border-[hsl(var(--border-color))]">
                  <td className="max-w-xs truncate px-4 py-3 font-medium">{job.title}</td>
                  <td className="px-4 py-3 text-[hsl(var(--muted))]">{job.companyName}</td>
                  <td className="px-4 py-3 text-[hsl(var(--muted))]">{formatEnumLabel(job.jobType)}</td>
                  <td className="px-4 py-3">
                    <AdminStatusBadge status={job.status} />
                  </td>
                  <td className="px-4 py-3">
                    {job.featured ? <Badge variant="primary">Featured</Badge> : <span className="text-[hsl(var(--muted))]">—</span>}
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex justify-end gap-1">
                      {job.status === "ARCHIVED" ? (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={restoreJob.isPending}
                          onClick={() => restoreJob.mutate(job.id)}
                          aria-label="Restore job"
                        >
                          <RotateCcw className="h-4 w-4 text-success-500" />
                        </Button>
                      ) : (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={removeJob.isPending}
                          onClick={() => removeJob.mutate(job.id)}
                          aria-label="Remove job"
                        >
                          <Archive className="h-4 w-4 text-danger-500" />
                        </Button>
                      )}
                      {job.featured ? (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={unfeatureJob.isPending}
                          onClick={() => unfeatureJob.mutate(job.id)}
                          aria-label="Unfeature job"
                        >
                          <StarOff className="h-4 w-4" />
                        </Button>
                      ) : (
                        <Button
                          variant="ghost"
                          size="sm"
                          isLoading={featureJob.isPending}
                          onClick={() => featureJob.mutate(job.id)}
                          aria-label="Feature job"
                        >
                          <Star className="h-4 w-4 text-warning-500" />
                        </Button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {data && !isFetching && (
        <Pagination pageNumber={data.pageNumber} totalPages={data.totalPages} onPageChange={setPage} />
      )}
    </div>
  );
}
