import { CheckCircle2 } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Skeleton } from "@/components/ui/skeleton";
import { useProfileCompletion } from "@/features/profile/hooks/useCandidateProfile";

export function ProfileCompletionCard() {
  const { data, isLoading } = useProfileCompletion();

  if (isLoading) {
    return (
      <Card>
        <Skeleton className="h-4 w-32" />
        <Skeleton className="mt-4 h-2 w-full" />
      </Card>
    );
  }

  const percentage = data?.profileCompletionPercentage ?? 0;
  const isComplete = percentage >= 100;

  return (
    <Card>
      <div className="flex items-center justify-between">
        <p className="text-sm font-medium text-[hsl(var(--muted))]">Profile Completion</p>
        {isComplete && <CheckCircle2 className="h-4 w-4 text-success-500" />}
      </div>
      <p className="mt-2 text-2xl font-semibold">{percentage}%</p>
      <Progress value={percentage} className="mt-3" />
      {!isComplete && (
        <p className="mt-2 text-xs text-[hsl(var(--muted))]">
          Complete your profile to improve your visibility to recruiters.
        </p>
      )}
    </Card>
  );
}
