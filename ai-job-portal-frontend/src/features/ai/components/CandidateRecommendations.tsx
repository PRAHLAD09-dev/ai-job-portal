import { useState } from "react";
import { ChevronDown, Eye, Sparkles, ThumbsDown, ThumbsUp } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Select } from "@/components/ui/select";
import { FormField } from "@/components/ui/form-field";
import { Button } from "@/components/ui/button";
import { Badge, type BadgeProps } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { useCandidateRecommendations } from "@/features/ai/hooks/useAi";
import { MatchBreakdownBars } from "@/features/ai/components/MatchBreakdownBars";
import {
  useRejectApplication,
  useShortlistApplication,
} from "@/features/recruiter-applications/hooks/useRecruiterApplications";
import type { JobSummaryResponse } from "@/features/jobs/types";
import type { CandidateRecommendationResponse } from "@/features/ai/types";

interface CandidateRecommendationsProps {
  jobs: JobSummaryResponse[];
  onSelectApplication: (applicationId: string) => void;
}

const HIRING_RECOMMENDATION_VARIANT: Record<CandidateRecommendationResponse["hiringRecommendation"], BadgeProps["variant"]> = {
  "Strongly Recommend": "success",
  Recommend: "primary",
  Consider: "warning",
  "Not a Fit": "danger",
};

/** One ranked applicant — collapsed shows the overall score and hiring signal;
 * expanded reveals the match breakdown, strengths, weaknesses, and missing
 * skills, per DAY06_FRONTEND_AI_ENHANCEMENT.md's "Recruiter Candidate Match". */
function CandidateCard({
  recommendation,
  onSelectApplication,
}: {
  recommendation: CandidateRecommendationResponse;
  onSelectApplication: (applicationId: string) => void;
}) {
  const [expanded, setExpanded] = useState(false);
  const shortlist = useShortlistApplication();
  const reject = useRejectApplication();

  return (
    <div className="rounded-lg border border-[hsl(var(--border-color))] p-3 text-sm">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <p className="font-medium">{recommendation.candidateName}</p>
          <Badge variant={HIRING_RECOMMENDATION_VARIANT[recommendation.hiringRecommendation]} className="mt-1">
            {recommendation.hiringRecommendation}
          </Badge>
        </div>
        <span className="rounded-full bg-primary-600/10 px-2 py-0.5 text-xs font-medium text-primary-600">
          {Math.round(recommendation.matchScore)}% match
        </span>
      </div>

      <button
        type="button"
        onClick={() => setExpanded((v) => !v)}
        className="mt-3 flex w-full items-center justify-between text-left text-xs text-primary-600"
      >
        <span>Match breakdown &amp; AI notes</span>
        <ChevronDown className={`h-3.5 w-3.5 transition-transform ${expanded ? "rotate-180" : ""}`} />
      </button>

      {expanded && (
        <div className="mt-3 space-y-4 border-t border-[hsl(var(--border-color))] pt-3">
          <MatchBreakdownBars breakdown={recommendation.matchBreakdown} />

          {recommendation.strengths.length > 0 && (
            <div>
              <p className="text-xs font-medium">Strengths</p>
              <div className="mt-1.5 flex flex-wrap gap-1.5">
                {recommendation.strengths.map((s, i) => (
                  <Badge key={i} variant="success">
                    {s}
                  </Badge>
                ))}
              </div>
            </div>
          )}

          {recommendation.weaknesses.length > 0 && (
            <div>
              <p className="text-xs font-medium">Weaknesses</p>
              <div className="mt-1.5 flex flex-wrap gap-1.5">
                {recommendation.weaknesses.map((s, i) => (
                  <Badge key={i} variant="danger">
                    {s}
                  </Badge>
                ))}
              </div>
            </div>
          )}

          {recommendation.missingSkills.length > 0 && (
            <div>
              <p className="text-xs font-medium">Missing Skills</p>
              <div className="mt-1.5 flex flex-wrap gap-1.5">
                {recommendation.missingSkills.map((s, i) => (
                  <Badge key={i} variant="warning">
                    {s}
                  </Badge>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      <div className="mt-3 flex flex-wrap gap-2">
        <Button size="sm" variant="outline" onClick={() => onSelectApplication(recommendation.applicationId)}>
          <Eye className="h-3.5 w-3.5" /> View Candidate
        </Button>
        <Button
          size="sm"
          variant="outline"
          isLoading={shortlist.isPending}
          onClick={() => shortlist.mutate(recommendation.applicationId)}
        >
          <ThumbsUp className="h-3.5 w-3.5" /> Shortlist
        </Button>
        <Button
          size="sm"
          variant="outline"
          isLoading={reject.isPending}
          onClick={() => reject.mutate({ applicationId: recommendation.applicationId })}
        >
          <ThumbsDown className="h-3.5 w-3.5" /> Reject
        </Button>
      </div>
    </div>
  );
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
                <CandidateCard key={rec.applicationId} recommendation={rec} onSelectApplication={onSelectApplication} />
              ))
          )}
        </div>
      )}
    </Card>
  );
}
