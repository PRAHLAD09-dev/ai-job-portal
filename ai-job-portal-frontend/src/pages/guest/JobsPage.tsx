import { useMemo, useState } from "react";
import { AlertTriangle, Search, SlidersHorizontal } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Modal } from "@/components/ui/modal";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { useDebouncedValue } from "@/hooks/useDebouncedValue";
import { useJobSearch } from "@/features/jobs/hooks/useJobs";
import { GuestJobCard } from "@/features/jobs/components/GuestJobCard";
import { JobFilters } from "@/features/jobs/components/JobFilters";
import type { JobSearchCriteria } from "@/features/jobs/types";

const PAGE_SIZE = 12;

/**
 * Public Job Listings page — GET /jobs/search (job-service's public
 * browsing/search endpoint, no auth required). Reuses the same
 * useJobSearch hook, JobFilters, and Pagination components as
 * CandidateJobsPage; the only difference is the card (GuestJobCard
 * instead of JobCard, since bookmarking is an authenticated feature)
 * and the absence of the Job Alerts / Saved Jobs shortcuts, which only
 * make sense for a signed-in candidate.
 */
export default function JobsPage() {
  const [keyword, setKeyword] = useState("");
  const [filters, setFilters] = useState<JobSearchCriteria>({});
  const [page, setPage] = useState(0);
  const [isFilterModalOpen, setIsFilterModalOpen] = useState(false);

  const debouncedKeyword = useDebouncedValue(keyword, 400);

  const searchParams = useMemo(
    () => ({ ...filters, keyword: debouncedKeyword || undefined, page, size: PAGE_SIZE }),
    [filters, debouncedKeyword, page],
  );

  const { data, isLoading, isFetching, isError, refetch } = useJobSearch(searchParams);

  const handleFiltersChange = (next: JobSearchCriteria) => {
    setFilters(next);
    setPage(0);
  };

  const handleResetFilters = () => {
    setFilters({});
    setPage(0);
  };

  return (
    <div className="mx-auto max-w-6xl space-y-6 px-4 py-12 md:px-8">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight md:text-3xl">Job Listings</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">
          {data ? `${data.totalElements} jobs found` : "Search across every published job"}
        </p>
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

          {!isLoading && isError && (
            <EmptyState
              icon={<AlertTriangle className="h-8 w-8" />}
              title="Couldn't load jobs"
              message="Something went wrong while fetching job listings. Please try again."
              actionLabel="Retry"
              onAction={() => refetch()}
            />
          )}

          {!isLoading && !isError && data?.content.length === 0 && (
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

          {!isLoading && !isError && data && data.content.length > 0 && (
            <>
              <div className={`grid gap-4 sm:grid-cols-2 xl:grid-cols-3 ${isFetching ? "opacity-60" : ""}`}>
                {data.content.map((job) => (
                  <GuestJobCard key={job.id} job={job} />
                ))}
              </div>
              <Pagination pageNumber={data.pageNumber} totalPages={data.totalPages} onPageChange={setPage} />
            </>
          )}
        </div>
      </div>

      <Modal open={isFilterModalOpen} onOpenChange={setIsFilterModalOpen} title="Filters">
        <JobFilters filters={filters} onChange={handleFiltersChange} onReset={handleResetFilters} />
        <div className="mt-4 flex justify-end">
          <Button onClick={() => setIsFilterModalOpen(false)}>Apply Filters</Button>
        </div>
      </Modal>
    </div>
  );
}
