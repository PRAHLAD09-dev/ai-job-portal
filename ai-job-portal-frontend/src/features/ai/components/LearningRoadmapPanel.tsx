import { BookOpen, Download, GraduationCap, ListOrdered, RefreshCw, Rocket, Sparkles } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/components/common/EmptyState";
import { useLearningRoadmap } from "@/features/ai/hooks/useAi";

const STAGES = [
  { key: "beginnerTopics", label: "Beginner", icon: BookOpen } as const,
  { key: "intermediateTopics", label: "Intermediate", icon: GraduationCap } as const,
  { key: "advancedTopics", label: "Advanced", icon: Rocket } as const,
];

/**
 * Beginner -> Intermediate -> Advanced learning path — GET /ai/learning-roadmap,
 * rendered as a timeline per DAY06_FRONTEND_AI_ENHANCEMENT.md's "Learning
 * Roadmap" section.
 */
export function LearningRoadmapPanel() {
  const { data, isLoading, isFetching, refetch } = useLearningRoadmap(true);

  const handleDownload = () => {
    if (!data) return;
    const text = [
      "Learning Roadmap",
      "",
      "BEGINNER",
      ...data.beginnerTopics.map((s) => `- ${s}`),
      "",
      "INTERMEDIATE",
      ...data.intermediateTopics.map((s) => `- ${s}`),
      "",
      "ADVANCED",
      ...data.advancedTopics.map((s) => `- ${s}`),
      "",
      "SUGGESTED RESOURCES",
      ...data.suggestedResources.map((s) => `- ${s}`),
      "",
      "PRACTICE ORDER",
      ...data.practiceOrder.map((s, i) => `${i + 1}. ${s}`),
    ].join("\n");
    const blob = new Blob([text], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "learning-roadmap.txt";
    a.click();
    URL.revokeObjectURL(url);
  };

  const hasAnyStage =
    !!data && (data.beginnerTopics.length > 0 || data.intermediateTopics.length > 0 || data.advancedTopics.length > 0);

  return (
    <Card>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <Sparkles className="h-5 w-5 text-primary-600" />
          <h2 className="text-lg font-semibold">Learning roadmap</h2>
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
        A step-by-step path from your current skills to where the job market wants you to be.
      </p>

      {isLoading && (
        <div className="mt-4 space-y-3">
          <Skeleton className="h-20 w-full" />
          <Skeleton className="h-20 w-full" />
          <Skeleton className="h-20 w-full" />
        </div>
      )}

      {!isLoading && data && hasAnyStage && (
        <div className="mt-6 space-y-6">
          {/* Timeline: Beginner -> Intermediate -> Advanced */}
          <ol className="relative space-y-6 border-l border-[hsl(var(--border-color))] pl-6">
            {STAGES.map(({ key, label, icon: Icon }) => {
              const topics = data[key];
              if (topics.length === 0) return null;
              return (
                <li key={key} className="relative">
                  <span className="absolute -left-[31px] flex h-6 w-6 items-center justify-center rounded-full bg-primary-600/10 text-primary-600 ring-4 ring-[hsl(var(--card))]">
                    <Icon className="h-3.5 w-3.5" />
                  </span>
                  <p className="text-sm font-semibold">{label}</p>
                  <div className="mt-2 flex flex-wrap gap-1.5">
                    {topics.map((topic, i) => (
                      <Badge key={i} variant="primary">
                        {topic}
                      </Badge>
                    ))}
                  </div>
                </li>
              );
            })}
          </ol>

          {data.suggestedResources.length > 0 && (
            <div>
              <p className="text-sm font-medium">Suggested Resources</p>
              <ul className="mt-2 list-disc space-y-1 pl-5 text-sm text-[hsl(var(--muted))]">
                {data.suggestedResources.map((s, i) => (
                  <li key={i}>{s}</li>
                ))}
              </ul>
            </div>
          )}

          {data.practiceOrder.length > 0 && (
            <div>
              <div className="flex items-center gap-1.5">
                <ListOrdered className="h-4 w-4 text-primary-600" />
                <p className="text-sm font-medium">Practice Order</p>
              </div>
              <ol className="mt-2 list-decimal space-y-1 pl-5 text-sm">
                {data.practiceOrder.map((s, i) => (
                  <li key={i}>{s}</li>
                ))}
              </ol>
            </div>
          )}
        </div>
      )}

      {!isLoading && (!data || !hasAnyStage) && (
        <div className="mt-4">
          <EmptyState
            icon={<Sparkles className="h-8 w-8" />}
            title="No roadmap yet"
            message="Complete your profile with skills so the AI can build a learning path for you."
          />
        </div>
      )}
    </Card>
  );
}
