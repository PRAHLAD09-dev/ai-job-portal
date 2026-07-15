import { Progress } from "@/components/ui/progress";
import type { MatchBreakdownResponse } from "@/features/ai/types";

const DIMENSIONS: { key: keyof MatchBreakdownResponse; label: string }[] = [
  { key: "skillMatch", label: "Skill" },
  { key: "experienceMatch", label: "Experience" },
  { key: "educationMatch", label: "Education" },
  { key: "projectMatch", label: "Project" },
  { key: "salaryMatch", label: "Salary" },
  { key: "locationMatch", label: "Location" },
];

/** Explainable AI dimension breakdown — per DAY10/DAY06's "AI Job Match %" section. */
export function MatchBreakdownBars({ breakdown }: { breakdown: MatchBreakdownResponse }) {
  return (
    <div className="space-y-2.5">
      {DIMENSIONS.map(({ key, label }) => {
        const score = breakdown[key];
        return (
          <div key={key}>
            <div className="flex justify-between text-xs text-[hsl(var(--muted))]">
              <span>{label} Match</span>
              <span>{score}%</span>
            </div>
            <Progress value={score} className="mt-1" />
          </div>
        );
      })}
    </div>
  );
}
