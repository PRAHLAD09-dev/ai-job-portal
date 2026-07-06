import { useState } from "react";
import { Bookmark } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { useSavedJobsList } from "@/features/jobs/hooks/useSavedJobs";
import { JobCard } from "@/features/jobs/components/JobCard";

const PAGE_SIZE = 12;

export default function SavedJobsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useSavedJobsList({ page, size: PAGE_SIZE });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Saved Jobs</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Jobs you have bookmarked for later.</p>
      </div>

      {isLoading && (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <Skeleton key={i} className="h-40 w-full" />
          ))}
        </div>
      )}

      {!isLoading && data?.content.length === 0 && (
        <EmptyState
          icon={<Bookmark className="h-8 w-8" />}
          title="No saved jobs yet"
          message="Bookmark jobs you're interested in to easily find them here later."
        />
      )}

      {!isLoading && data && data.content.length > 0 && (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {data.content.map((entry) => (
              <JobCard key={entry.id} job={entry.job} />
            ))}
          </div>
          <Pagination pageNumber={data.pageNumber} totalPages={data.totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}
