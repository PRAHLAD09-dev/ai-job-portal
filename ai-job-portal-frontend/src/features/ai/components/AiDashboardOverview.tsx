import { FileText, Sparkles, TrendingUp } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useJobRecommendations, useLatestResumeAnalysis, useSkillGap } from "@/features/ai/hooks/useAi";

interface AiDashboardOverviewProps {
  onNavigate: (tab: string) => void;
}

/**
 * AI Dashboard summary — combines the latest resume analysis, skill gap,
 * and job recommendation count into quick-glance stat cards, each backed
 * by a real ai-service call (no AI usage history endpoint exists yet for
 * candidates, so that metric isn't shown — see KNOWN_BACKEND_LIMITATIONS.md).
 */
export function AiDashboardOverview({ onNavigate }: AiDashboardOverviewProps) {
  const { data: analysis, isLoading: isLoadingAnalysis, isError: noAnalysis } = useLatestResumeAnalysis();
  const { data: recommendations, isLoading: isLoadingRecs } = useJobRecommendations(true);
  const { data: skillGap, isLoading: isLoadingGap } = useSkillGap(true);

  return (
    <div className="grid gap-4 sm:grid-cols-3">
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
    </div>
  );
}
