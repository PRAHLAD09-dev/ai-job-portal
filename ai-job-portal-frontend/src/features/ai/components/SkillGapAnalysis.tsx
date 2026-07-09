import { Download, RefreshCw, TrendingUp } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { useSkillGap } from "@/features/ai/hooks/useAi";

/**
 * Skill gap analysis — GET /ai/skills/gap. Per KNOWN_BACKEND_LIMITATIONS.md
 * (Day 04): the response has currentSkills/missingSkills/careerSuggestions
 * only — no learning roadmap or recommended-courses fields exist in
 * SkillGapResponse, so those sections aren't rendered rather than being
 * invented client-side.
 */
export function SkillGapAnalysis() {
  const { data, isLoading, isFetching, refetch } = useSkillGap(true);

  const handleDownload = () => {
    if (!data) return;
    const text = [
      "Skill Gap Report",
      "",
      "CURRENT SKILLS",
      ...data.currentSkills.map((s) => `- ${s}`),
      "",
      "MISSING SKILLS",
      ...data.missingSkills.map((s) => `- ${s}`),
      "",
      "CAREER SUGGESTIONS",
      ...data.careerSuggestions.map((s) => `- ${s}`),
    ].join("\n");
    const blob = new Blob([text], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "skill-gap-report.txt";
    a.click();
    URL.revokeObjectURL(url);
  };

  const total = (data?.currentSkills.length ?? 0) + (data?.missingSkills.length ?? 0);
  const currentPct = total > 0 ? Math.round(((data?.currentSkills.length ?? 0) / total) * 100) : 0;

  return (
    <Card>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <TrendingUp className="h-5 w-5 text-primary-600" />
          <h2 className="text-lg font-semibold">Skill gap analysis</h2>
        </div>
        <div className="flex gap-2">
          <Button size="sm" variant="outline" isLoading={isFetching} onClick={() => refetch()}>
            <RefreshCw className="h-3.5 w-3.5" /> Refresh
          </Button>
          {data && (
            <Button size="sm" variant="outline" onClick={handleDownload}>
              <Download className="h-3.5 w-3.5" /> Download report
            </Button>
          )}
        </div>
      </div>
      <p className="mt-1 text-sm text-[hsl(var(--muted))]">
        Based on your profile skills against currently open jobs on the platform.
      </p>

      {isLoading && (
        <div className="mt-4 space-y-3">
          <Skeleton className="h-3 w-full" />
          <Skeleton className="h-20 w-full" />
          <Skeleton className="h-20 w-full" />
        </div>
      )}

      {!isLoading && data && (
        <div className="mt-5 space-y-5">
          {total > 0 && (
            <div>
              <div className="flex justify-between text-xs text-[hsl(var(--muted))]">
                <span>Skills you have</span>
                <span>{currentPct}%</span>
              </div>
              <div className="mt-1 h-2 w-full overflow-hidden rounded-full bg-danger-500/20">
                <div className="h-full rounded-full bg-success-500" style={{ width: `${currentPct}%` }} />
              </div>
            </div>
          )}

          <div>
            <p className="text-sm font-medium">Current Skills</p>
            {data.currentSkills.length === 0 ? (
              <p className="mt-1 text-sm text-[hsl(var(--muted))]">No skills found on your profile yet.</p>
            ) : (
              <div className="mt-2 flex flex-wrap gap-1.5">
                {data.currentSkills.map((s, i) => (
                  <Badge key={i} variant="success">
                    {s}
                  </Badge>
                ))}
              </div>
            )}
          </div>

          <div>
            <p className="text-sm font-medium">Missing Skills</p>
            {data.missingSkills.length === 0 ? (
              <p className="mt-1 text-sm text-[hsl(var(--muted))]">No major skill gaps found — nice work!</p>
            ) : (
              <div className="mt-2 flex flex-wrap gap-1.5">
                {data.missingSkills.map((s, i) => (
                  <Badge key={i} variant="danger">
                    {s}
                  </Badge>
                ))}
              </div>
            )}
          </div>

          <div>
            <p className="text-sm font-medium">Career Suggestions</p>
            {data.careerSuggestions.length === 0 ? (
              <p className="mt-1 text-sm text-[hsl(var(--muted))]">No career suggestions available right now.</p>
            ) : (
              <ul className="mt-2 list-disc space-y-1 pl-5 text-sm">
                {data.careerSuggestions.map((s, i) => (
                  <li key={i}>{s}</li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}

      {!isLoading && !data && (
        <div className="mt-4">
          <EmptyState title="No data available" message="Skill gap analysis couldn't be generated right now." />
        </div>
      )}
    </Card>
  );
}
