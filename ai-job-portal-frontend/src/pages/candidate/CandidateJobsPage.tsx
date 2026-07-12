import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Bell, Bookmark, Search, SlidersHorizontal } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button, buttonVariants } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Modal } from "@/components/ui/modal";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { ROUTES } from "@/constants/routes";
import { useDebouncedValue } from "@/hooks/useDebouncedValue";
import { useJobSearch } from "@/features/jobs/hooks/useJobs";
import { JobCard } from "@/features/jobs/components/JobCard";
import { JobFilters } from "@/features/jobs/components/JobFilters";
import type { JobSearchCriteria } from "@/features/jobs/types";

const PAGE_SIZE = 12;

export default function CandidateJobsPage() {
  const [keyword, setKeyword] = useState("");
  const [filters, setFilters] = useState<JobSearchCriteria>({});
  const [page, setPage] = useState(0);
  const [isFilterModalOpen, setIsFilterModalOpen] = useState(false);

  const debouncedKeyword = useDebouncedValue(keyword, 400);

  const searchParams = useMemo(
    () => ({ ...filters, keyword: debouncedKeyword || undefined, page, size: PAGE_SIZE }),
    [filters, debouncedKeyword, page],
  );

  const { data, isLoading, isFetching } = useJobSearch(searchParams);

  const handleFiltersChange = (next: JobSearchCriteria) => {
    setFilters(next);
    setPage(0);
  };

  const handleResetFilters = () => {
    setFilters({});
    setPage(0);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Find Jobs</h1>
          <p className="mt-1 text-sm text-[hsl(var(--muted))]">
            {data ? `${data.totalElements} jobs found` : "Search across every published job"}
          </p>
        </div>
        <div className="flex gap-2">
          <Link to={ROUTES.CANDIDATE_JOB_ALERTS} className={buttonVariants({ variant: "outline" })}>
            <Bell className="h-4 w-4" /> Job Alerts
          </Link>
          <Link to={ROUTES.CANDIDATE_SAVED_JOBS} className={buttonVariants({ variant: "outline" })}>
            <Bookmark className="h-4 w-4" /> Saved Jobs
          </Link>
        </div>
      </div>

      <div className="flex gap-3">
        <div className="relative flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
          <Input
            placeholder="Search by job title, company, or keyword"
            className="pl-9"
            value={keyword}
            onChange={(e) => {
              setKeyword(e.target.value);
              setPage(0);
            }}
          />
        </div>
        <Button variant="outline" className="lg:hidden" onClick={() => setIsFilterModalOpen(true)}>
          <SlidersHorizontal className="h-4 w-4" /> Filters
        </Button>
      </div>

      <div className="grid gap-6 lg:grid-cols-[260px_1fr]">
        <aside className="hidden lg:block">
          <Card>
            <JobFilters filters={filters} onChange={handleFiltersChange} onReset={handleResetFilters} />
          </Card>
        </aside>

        <div className="space-y-4">
          {isLoading && (
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {Array.from({ length: 6 }).map((_, i) => (
                <Skeleton key={i} className="h-40 w-full" />
              ))}
            </div>
          )}

          {!isLoading && data?.content.length === 0 && (
            <EmptyState
              icon={<Search className="h-8 w-8" />}
              title="No jobs found"
              message="Try adjusting your search or filters to find more opportunities."
              actionLabel="Reset Filters"
              onAction={() => {
                handleResetFilters();
                setKeyword("");
              }}
            />
          )}

          {!isLoading && data && data.content.length > 0 && (
            <>
              <div className={`grid gap-4 sm:grid-cols-2 xl:grid-cols-3 ${isFetching ? "opacity-60" : ""}`}>
                {data.content.map((job) => (
                  <JobCard key={job.id} job={job} />
                ))}
              </div>
              <Pagination pageNumber={data.pageNumber} totalPages={data.totalPages} onPageChange={setPage} />
            </>
          )}
        </div>
      </div>

      <Modal open={isFilterModalOpen} onOpenChange={setIsFilterModalOpen} title="Filters">
        <JobFilters
          filters={filters}
          onChange={handleFiltersChange}
          onReset={() => {
            handleResetFilters();
          }}
        />
        <div className="mt-4 flex justify-end">
          <Button onClick={() => setIsFilterModalOpen(false)}>Apply Filters</Button>
        </div>
      </Modal>
    </div>
  );
}
