import { useState } from "react";
import { Link } from "react-router-dom";
import { RefreshCw, Search, Sparkles } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Input } from "@/components/ui/input";
import { EmptyState } from "@/components/common/EmptyState";
import { buildRoute } from "@/constants/routes";
import { useJobRecommendations } from "@/features/ai/hooks/useAi";
import type { JobRecommendationResponse } from "@/features/ai/types";

function scoreVariant(score: number) {
  if (score >= 75) return "text-success-500";
  if (score >= 50) return "text-warning-500";
  return "text-danger-500";
}

function RecommendationCard({ recommendation }: { recommendation: JobRecommendationResponse }) {
  return (
    <Link to={buildRoute.candidateJobDetails(recommendation.jobId)}>
      <Card className="transition-shadow hover:shadow-md">
        <div className="flex items-start justify-between gap-3">
          <div className="min-w-0">
            <p className="truncate font-semibold">{recommendation.jobTitle}</p>
            <p className="truncate text-sm text-[hsl(var(--muted))]">{recommendation.companyName}</p>
          </div>
          <div className={`shrink-0 text-right ${scoreVariant(recommendation.matchScore)}`}>
            <p className="text-lg font-semibold">{recommendation.matchScore}%</p>
            <p className="text-[11px] text-[hsl(var(--muted))]">match</p>
          </div>
        </div>
        <p className="mt-3 line-clamp-2 text-sm text-[hsl(var(--muted))]">{recommendation.reasoning}</p>
      </Card>
    </Link>
  );
}

/**
 * Job recommendations for the authenticated candidate — POST /ai/jobs/recommend.
 * Per KNOWN_BACKEND_LIMITATIONS.md (Day 04): this endpoint takes no query
 * params, so server-side pagination/filtering/sorting isn't possible; the
 * search box below filters client-side within the returned batch only.
 */
export function JobRecommendations() {
  const [keyword, setKeyword] = useState("");
  const { data, isLoading, isFetching, refetch } = useJobRecommendations(true);

  const filtered = (data ?? []).filter((r) => {
    const q = keyword.trim().toLowerCase();
    if (!q) return true;
    return r.jobTitle.toLowerCase().includes(q) || r.companyName.toLowerCase().includes(q);
  });

  return (
    <Card>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <Sparkles className="h-5 w-5 text-primary-600" />
          <h2 className="text-lg font-semibold">Recommended jobs for you</h2>
        </div>
        <Button size="sm" variant="outline" isLoading={isFetching} onClick={() => refetch()}>
          <RefreshCw className="h-3.5 w-3.5" /> Refresh
        </Button>
      </div>

      {data && data.length > 0 && (
        <div className="relative mt-4">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
          <Input
            placeholder="Search these recommendations by title or company..."
            className="pl-9"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
        </div>
      )}

      {isLoading && (
        <div className="mt-4 grid gap-3 sm:grid-cols-2">
          <Skeleton className="h-28 w-full" />
          <Skeleton className="h-28 w-full" />
          <Skeleton className="h-28 w-full" />
          <Skeleton className="h-28 w-full" />
        </div>
      )}

      {!isLoading && (!data || data.length === 0) && (
        <div className="mt-4">
          <EmptyState
            icon={<Sparkles className="h-8 w-8" />}
            title="No recommendations yet"
            message="Complete your profile with skills and experience so the AI can match you with relevant jobs."
          />
        </div>
      )}

      {!isLoading && data && data.length > 0 && filtered.length === 0 && (
        <div className="mt-4">
          <EmptyState title="No matches" message="No recommendations match your search." />
        </div>
      )}

      {filtered.length > 0 && (
        <div className="mt-4 grid gap-3 sm:grid-cols-2">
          {filtered.map((r) => (
            <RecommendationCard key={r.jobId} recommendation={r} />
          ))}
        </div>
      )}
    </Card>
  );
}
