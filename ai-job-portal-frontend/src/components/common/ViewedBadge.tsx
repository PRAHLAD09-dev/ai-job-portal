import { Eye, EyeOff } from "lucide-react";
import { Badge } from "@/components/ui/badge";

interface ViewedBadgeProps {
  viewed: boolean;
  viewedAt?: string | null;
  className?: string;
}

/**
 * DAY11/DAY07 "Viewed by Recruiter": shows whether a recruiter has
 * opened this application yet, and when. Used on the candidate's
 * application detail/timeline and the recruiter's application list.
 */
export function ViewedBadge({ viewed, viewedAt, className }: ViewedBadgeProps) {
  if (!viewed) {
    return (
      <Badge variant="outline" className={className}>
        <EyeOff className="h-3 w-3" /> Not viewed yet
      </Badge>
    );
  }

  return (
    <Badge variant="success" className={className}>
      <Eye className="h-3 w-3" />
      Viewed{viewedAt ? ` ${new Date(viewedAt).toLocaleString()}` : ""}
    </Badge>
  );
}
