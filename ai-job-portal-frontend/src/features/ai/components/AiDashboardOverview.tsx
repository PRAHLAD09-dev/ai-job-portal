import { useMemo } from "react";
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { FileText, Map, Sparkles, TrendingUp } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useJobRecommendations, useLatestResumeAnalysis, useLearningRoadmap, useSkillGap } from "@/features/ai/hooks/useAi";
import type { MatchBreakdownResponse } from "@/features/ai/types";

interface AiDashboardOverviewProps {
  onNavigate: (tab: string) => void;
}

const BREAKDOWN_DIMENSIONS: { key: keyof MatchBreakdownResponse; label: string }[] = [
  { key: "skillMatch", label: "Skill" },
  { key: "experienceMatch", label: "Experience" },
  { key: "educationMatch", label: "Education" },
  { key: "projectMatch", label: "Project" },
  { key: "salaryMatch", label: "Salary" },
  { key: "locationMatch", label: "Location" },
];

/**
 * AI Dashboard summary — combines the latest resume analysis, skill gap,
 * learning roadmap, and job recommendation count into quick-glance stat
 * cards, plus two charts built from data ai-service already returns:
 * "Match Breakdown" (average of the six dimension scores across the
 * candidate's current job recommendations) and "Skill Distribution"
 * (current vs. missing skill counts). Per KNOWN_BACKEND_LIMITATIONS.md,
 * there is no AI-usage-history endpoint for candidates yet, so historical
 * trend charts aren't shown — everything here is derived from live data.
 */
export function AiDashboardOverview({ onNavigate }: AiDashboardOverviewProps) {
  const { data: analysis, isLoading: isLoadingAnalysis, isError: noAnalysis } = useLatestResumeAnalysis();
  const { data: recommendations, isLoading: isLoadingRecs } = useJobRecommendations(true);
  const { data: skillGap, isLoading: isLoadingGap } = useSkillGap(true);
  const { data: roadmap, isLoading: isLoadingRoadmap } = useLearningRoadmap(true);

  const breakdownChartData = useMemo(() => {
    if (!recommendations || recommendations.length === 0) return [];
    return BREAKDOWN_DIMENSIONS.map(({ key, label }) => {
      const sum = recommendations.reduce((acc, r) => acc + (r.matchBreakdown[key] ?? 0), 0);
      return { dimension: label, score: Math.round(sum / recommendations.length) };
    });
  }, [recommendations]);

  const skillDistributionData = useMemo(() => {
    if (!skillGap) return [];
    return [
      { label: "Have", count: skillGap.currentSkills.length },
      { label: "Missing", count: skillGap.missingSkills.length },
    ];
  }, [skillGap]);

  return (
    <div className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card className="cursor-pointer transition-shadow hover:shadow-md" onClick={() => onNavigate("resume")}>
          <div className="flex items-center gap-2 text-primary-600">
            <FileText className="h-4 w-4" />
            <p className="text-sm font-medium">Resume / ATS Score</p>
          </div>
          {isLoadingAnalysis ? (
            <Skeleton className="mt-3 h-8 w-16" />
          ) : noAnalysis || !analysis ? (
            <p className="mt-2 text-sm text-[hsl(var(--muted))]">Not analyzed yet</p>
          ) : (
            <p className="mt-2 text-2xl font-semibold">{analysis.atsScore}/100</p>
          )}
          <p className="mt-1 text-xs text-primary-600">Analyze resume →</p>
        </Card>

        <Card className="cursor-pointer transition-shadow hover:shadow-md" onClick={() => onNavigate("jobs")}>
          <div className="flex items-center gap-2 text-primary-600">
            <Sparkles className="h-4 w-4" />
            <p className="text-sm font-medium">Recommended Jobs</p>
          </div>
          {isLoadingRecs ? (
            <Skeleton className="mt-3 h-8 w-16" />
          ) : (
            <p className="mt-2 text-2xl font-semibold">{recommendations?.length ?? 0}</p>
          )}
          <p className="mt-1 text-xs text-primary-600">View matches →</p>
        </Card>

        <Card className="cursor-pointer transition-shadow hover:shadow-md" onClick={() => onNavigate("skills")}>
          <div className="flex items-center gap-2 text-primary-600">
            <TrendingUp className="h-4 w-4" />
            <p className="text-sm font-medium">Skill Gap</p>
          </div>
          {isLoadingGap ? (
            <Skeleton className="mt-3 h-8 w-16" />
          ) : (
            <p className="mt-2 text-2xl font-semibold">{skillGap?.missingSkills.length ?? 0} missing</p>
          )}
          <p className="mt-1 text-xs text-primary-600">See breakdown →</p>
        </Card>

        <Card className="cursor-pointer transition-shadow hover:shadow-md" onClick={() => onNavigate("roadmap")}>
          <div className="flex items-center gap-2 text-primary-600">
            <Map className="h-4 w-4" />
            <p className="text-sm font-medium">Learning Roadmap</p>
          </div>
          {isLoadingRoadmap ? (
            <Skeleton className="mt-3 h-8 w-16" />
          ) : (
            <p className="mt-2 text-2xl font-semibold">
              {(roadmap?.beginnerTopics.length ?? 0) +
                (roadmap?.intermediateTopics.length ?? 0) +
                (roadmap?.advancedTopics.length ?? 0)}{" "}
              topics
            </p>
          )}
          <p className="mt-1 text-xs text-primary-600">View roadmap →</p>
        </Card>
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <Card>
          <h3 className="text-base font-semibold">Match breakdown</h3>
          <p className="mt-1 text-xs text-[hsl(var(--muted))]">
            Average of your current job recommendations, by dimension.
          </p>
          {isLoadingRecs ? (
            <Skeleton className="mt-4 h-56 w-full" />
          ) : breakdownChartData.length === 0 ? (
            <p className="mt-6 text-sm text-[hsl(var(--muted))]">No recommendations yet to chart.</p>
          ) : (
            <div className="mt-4 h-56">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={breakdownChartData}>
                  <CartesianGrid strokeDasharray="3 3" opacity={0.2} />
                  <XAxis dataKey="dimension" tick={{ fontSize: 11 }} />
                  <YAxis domain={[0, 100]} tick={{ fontSize: 12 }} />
                  <Tooltip />
                  <Bar dataKey="score" fill="var(--color-primary-600)" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </Card>

        <Card>
          <h3 className="text-base font-semibold">Skill distribution</h3>
          <p className="mt-1 text-xs text-[hsl(var(--muted))]">Skills you have vs. skills the market wants.</p>
          {isLoadingGap ? (
            <Skeleton className="mt-4 h-56 w-full" />
          ) : skillDistributionData.length === 0 || (skillGap?.currentSkills.length === 0 && skillGap?.missingSkills.length === 0) ? (
            <p className="mt-6 text-sm text-[hsl(var(--muted))]">No skill data yet to chart.</p>
          ) : (
            <div className="mt-4 h-56">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={skillDistributionData}>
                  <CartesianGrid strokeDasharray="3 3" opacity={0.2} />
                  <XAxis dataKey="label" tick={{ fontSize: 12 }} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                  <Tooltip />
                  <Bar dataKey="count" fill="var(--color-primary-600)" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
}
