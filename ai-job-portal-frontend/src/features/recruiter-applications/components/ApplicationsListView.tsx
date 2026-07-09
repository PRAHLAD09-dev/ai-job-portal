import { useState } from "react";
import { Search, Users } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { useDebouncedValue } from "@/hooks/useDebouncedValue";
import { formatEnumLabel } from "@/utils/format";
import { useRecruiterApplications } from "@/features/recruiter-applications/hooks/useRecruiterApplications";
import { ApplicationStatusBadge } from "@/features/recruiter-applications/components/ApplicationStatusBadge";
import type { ApplicationStatus } from "@/features/applications/types";
import type { JobSummaryResponse } from "@/features/jobs/types";

const STATUS_OPTIONS: Array<ApplicationStatus | "ALL"> = [
  "ALL",
  "APPLIED",
  "UNDER_REVIEW",
  "SHORTLISTED",
  "INTERVIEW",
  "OFFERED",
  "HIRED",
  "REJECTED",
  "WITHDRAWN",
];

const PAGE_SIZE = 10;

interface ApplicationsListViewProps {
  jobs: JobSummaryResponse[];
  onSelectApplication: (applicationId: string) => void;
}

export function ApplicationsListView({ jobs, onSelectApplication }: ApplicationsListViewProps) {
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState<ApplicationStatus | "ALL">("ALL");
  const [jobId, setJobId] = useState<string>("ALL");
  const debouncedKeyword = useDebouncedValue(keyword, 300);

  const { data, isLoading, isFetching } = useRecruiterApplications({
    page,
    size: PAGE_SIZE,
    keyword: debouncedKeyword || undefined,
    status: status === "ALL" ? undefined : status,
    jobId: jobId === "ALL" ? undefined : jobId,
  });

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-3 sm:flex-row">
        <div className="relative flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
          <Input
            className="pl-9"
            placeholder="Search by candidate name..."
            value={keyword}
            onChange={(e) => {
              setKeyword(e.target.value);
              setPage(0);
            }}
          />
        </div>
        <Select
          className="sm:w-48"
          value={jobId}
          onChange={(e) => {
            setJobId(e.target.value);
            setPage(0);
          }}
        >
          <option value="ALL">All jobs</option>
          {jobs.map((job) => (
            <option key={job.id} value={job.id}>
              {job.title}
            </option>
          ))}
        </Select>
        <Select
          className="sm:w-48"
          value={status}
          onChange={(e) => {
            setStatus(e.target.value as ApplicationStatus | "ALL");
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

      {isLoading ? (
        <div className="space-y-2">
          <Skeleton className="h-14 w-full" />
          <Skeleton className="h-14 w-full" />
        </div>
      ) : !data || data.content.length === 0 ? (
        <EmptyState icon={<Users className="h-10 w-10" />} title="No data available" message="No applications match your current filters." />
      ) : (
        <div className="overflow-x-auto rounded-xl border border-[hsl(var(--border-color))]">
          <table className="w-full text-left text-sm">
            <thead className="bg-[hsl(var(--surface))] text-xs uppercase text-[hsl(var(--muted))]">
              <tr>
                <th className="px-4 py-3 font-medium">Candidate</th>
                <th className="px-4 py-3 font-medium">Job</th>
                <th className="px-4 py-3 font-medium">Applied</th>
                <th className="px-4 py-3 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((application) => (
                <tr
                  key={application.id}
                  className="cursor-pointer border-t border-[hsl(var(--border-color))] hover:bg-[hsl(var(--background))]"
                  onClick={() => onSelectApplication(application.id)}
                >
                  <td className="px-4 py-3 font-medium">{application.candidateName}</td>
                  <td className="max-w-xs truncate px-4 py-3 text-[hsl(var(--muted))]">{application.jobTitle}</td>
                  <td className="px-4 py-3 text-[hsl(var(--muted))]">
                    {new Date(application.appliedAt).toLocaleDateString()}
                  </td>
                  <td className="px-4 py-3">
                    <ApplicationStatusBadge status={application.status} />
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
