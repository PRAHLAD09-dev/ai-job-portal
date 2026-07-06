import { useState } from "react";
import { FileSearch } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { Pagination } from "@/components/common/Pagination";
import { useMyApplications } from "@/features/applications/hooks/useApplications";
import { ApplicationCard } from "@/features/applications/components/ApplicationCard";

const PAGE_SIZE = 10;

export default function CandidateApplicationsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useMyApplications({ page, size: PAGE_SIZE });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">My Applications</h1>
        <p className="mt-1 text-sm text-[hsl(var(--muted))]">Track the status of every job you have applied to.</p>
      </div>

      {isLoading && (
        <div className="space-y-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-24 w-full" />
          ))}
        </div>
      )}

      {!isLoading && data?.content.length === 0 && (
        <EmptyState
          icon={<FileSearch className="h-8 w-8" />}
          title="No applications yet"
          message="Start applying to jobs to see your application history here."
        />
      )}

      {!isLoading && data && data.content.length > 0 && (
        <>
          <div className="space-y-3">
            {data.content.map((application) => (
              <ApplicationCard key={application.id} application={application} />
            ))}
          </div>
          <Pagination pageNumber={data.pageNumber} totalPages={data.totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}
