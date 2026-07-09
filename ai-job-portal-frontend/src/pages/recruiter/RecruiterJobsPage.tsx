import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Briefcase, Plus, Search } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { useDebouncedValue } from "@/hooks/useDebouncedValue";
import { ROUTES } from "@/constants/routes";
import { formatEnumLabel } from "@/utils/format";
import { useMyCompanyJobs, useRecruiterJobStatistics } from "@/features/recruiter-jobs/hooks/useRecruiterJobs";
import { RecruiterJobsTable } from "@/features/recruiter-jobs/components/RecruiterJobsTable";
import type { JobStatus } from "@/features/jobs/types";

const STATUS_OPTIONS: Array<JobStatus | "ALL"> = ["ALL", "DRAFT", "PUBLISHED", "CLOSED", "ARCHIVED"];
const SORT_OPTIONS = [
  { value: "createdAt,desc", label: "Newest first" },
  { value: "createdAt,asc", label: "Oldest first" },
  { value: "title,asc", label: "Title (A–Z)" },
  { value: "title,desc", label: "Title (Z–A)" },
];
const PAGE_SIZE = 10;

export default function RecruiterJobsPage() {
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState<JobStatus | "ALL">("ALL");
  const [sort, setSort] = useState(SORT_OPTIONS[0].value);
  const debouncedKeyword = useDebouncedValue(keyword, 300);

  const { data, isLoading, isFetching } = useMyCompanyJobs({ page, size: PAGE_SIZE, sort });
  const { data: statistics } = useRecruiterJobStatistics();

  const filteredJobs = useMemo(() => {
    if (!data) return [];
    return data.content.filter((job) => {
      const matchesKeyword = job.title.toLowerCase().includes(debouncedKeyword.toLowerCase());
      const matchesStatus = status === "ALL" || job.status === status;
      return matchesKeyword && matchesStatus;
    });
  }, [data, debouncedKeyword, status]);

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Jobs</h1>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">Create, publish, and manage your company's job postings.</p>
        </div>
        <Link to={ROUTES.RECRUITER_CREATE_JOB}>
          <Button>
            <Plus className="h-4 w-4" /> Create job
          </Button>
        </Link>
      </div>

      {statistics && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <Card>
            <p className="text-sm text-[hsl(var(--muted))]">Total jobs</p>
            <p className="mt-2 text-2xl font-semibold">{statistics.totalJobs}</p>
          </Card>
          <Card>
            <p className="text-sm text-[hsl(var(--muted))]">Active</p>
            <p className="mt-2 text-2xl font-semibold">{statistics.activeJobs}</p>
          </Card>
          <Card>
            <p className="text-sm text-[hsl(var(--muted))]">Closed</p>
            <p className="mt-2 text-2xl font-semibold">{statistics.closedJobs}</p>
          </Card>
          <Card>
            <p className="text-sm text-[hsl(var(--muted))]">Drafts</p>
            <p className="mt-2 text-2xl font-semibold">{statistics.draftJobs}</p>
          </Card>
        </div>
      )}

      <div className="flex flex-col gap-3 sm:flex-row">
        <div className="relative flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
          <Input
            className="pl-9"
            placeholder="Search jobs on this page by title..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
        </div>
        <Select
          className="sm:w-52"
          value={status}
          onChange={(e) => setStatus(e.target.value as JobStatus | "ALL")}
        >
          {STATUS_OPTIONS.map((s) => (
            <option key={s} value={s}>
              {s === "ALL" ? "All statuses" : formatEnumLabel(s)}
            </option>
          ))}
        </Select>
        <Select className="sm:w-48" value={sort} onChange={(e) => setSort(e.target.value)}>
          {SORT_OPTIONS.map((s) => (
            <option key={s.value} value={s.value}>
              {s.label}
            </option>
          ))}
        </Select>
      </div>

      {isLoading ? (
        <div className="space-y-2">
          <Skeleton className="h-14 w-full" />
          <Skeleton className="h-14 w-full" />
          <Skeleton className="h-14 w-full" />
        </div>
      ) : filteredJobs.length === 0 ? (
        <EmptyState
          icon={<Briefcase className="h-10 w-10" />}
          title="No jobs found"
          message={
            data && data.content.length > 0
              ? "No jobs on this page match your search or filter."
              : "You haven't created any jobs yet. Post your first job to start receiving applications."
          }
          actionLabel="Create job"
          onAction={() => {
            window.location.href = ROUTES.RECRUITER_CREATE_JOB;
          }}
        />
      ) : (
        <RecruiterJobsTable jobs={filteredJobs} />
      )}

      {data && !isFetching && (
        <Pagination pageNumber={data.pageNumber} totalPages={data.totalPages} onPageChange={setPage} />
      )}
    </div>
  );
}
