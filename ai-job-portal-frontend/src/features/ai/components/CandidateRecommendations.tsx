import { useState } from "react";
import { Sparkles } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { useCandidateRecommendations } from "@/features/ai/hooks/useAi";
import type { JobSummaryResponse } from "@/features/jobs/types";

interface CandidateRecommendationsProps {
  jobs: JobSummaryResponse[];
  onSelectApplication: (applicationId: string) => void;
}

export function CandidateRecommendations({ jobs, onSelectApplication }: CandidateRecommendationsProps) {
  const [jobId, setJobId] = useState("");
  const recommend = useCandidateRecommendations(jobId);

  return (
    <Card>
      <div className="flex items-center gap-2">
        <Sparkles className="h-5 w-5 text-primary-600" />
        <h2 className="text-lg font-semibold">Candidate recommendations</h2>
      </div>
      <div className="mt-4 flex flex-wrap items-end gap-3">
        <div className="flex-1">
          <FormField label="Job" htmlFor="cr-job">
            <Select id="cr-job" value={jobId} onChange={(e) => setJobId(e.target.value)}>
              <option value="">Select a job</option>
              {jobs.map((job) => (
                <option key={job.id} value={job.id}>
                  {job.title}
                </option>
              ))}
            </Select>
          </FormField>
        </div>
        <Button disabled={!jobId} isLoading={recommend.isPending} onClick={() => jobId && recommend.mutate(jobId)}>
          <Sparkles className="h-4 w-4" /> Get recommendations
        </Button>
      </div>

      {recommend.isPending && (
        <div className="mt-4 space-y-2">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      )}

      {recommend.data && !recommend.isPending && (
        <div className="mt-6 space-y-3">
          {recommend.data.length === 0 ? (
            <EmptyState title="No data available" message="No candidate recommendations for this job yet." />
          ) : (
            recommend.data
              .slice()
              .sort((a, b) => b.matchScore - a.matchScore)
              .map((rec) => (
                <button
                  key={rec.applicationId}
                  type="button"
                  onClick={() => onSelectApplication(rec.applicationId)}
                  className="w-full rounded-lg border border-[hsl(var(--border-color))] p-3 text-left text-sm transition hover:border-primary-500"
                >
                  <div className="flex items-center justify-between">
                    <p className="font-medium">{rec.candidateName}</p>
                    <span className="rounded-full bg-primary-600/10 px-2 py-0.5 text-xs font-medium text-primary-600">
                      {Math.round(rec.matchScore)}% match
                    </span>
                  </div>
                  <p className="mt-1 text-[hsl(var(--muted))]">{rec.reasoning}</p>
                </button>
              ))
          )}
        </div>
      )}
    </Card>
  );
}
