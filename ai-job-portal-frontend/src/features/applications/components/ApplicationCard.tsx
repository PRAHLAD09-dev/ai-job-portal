import { memo } from "react";
import { Link } from "react-router-dom";
import { Building2, Calendar, Eye } from "lucide-react";
import { Card } from "@/components/ui/card";
import { StatusBadge } from "@/features/applications/components/StatusBadge";
import { buildRoute } from "@/constants/routes";
import type { ApplicationSummaryResponse } from "@/features/applications/types";

function ApplicationCardComponent({ application }: { application: ApplicationSummaryResponse }) {
  return (
    <Link to={buildRoute.candidateApplicationDetails(application.id)}>
      <Card className="transition-shadow hover:shadow-md">
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0">
            <p className="truncate font-semibold">{application.jobTitle}</p>
            <p className="mt-1 flex items-center gap-1 text-sm text-[hsl(var(--muted))]">
              <Building2 className="h-3.5 w-3.5" /> {application.companyName}
            </p>
            <p className="mt-1 flex items-center gap-1 text-xs text-[hsl(var(--muted))]">
              <Calendar className="h-3.5 w-3.5" /> Applied {new Date(application.appliedAt).toLocaleDateString()}
            </p>
            {application.viewed && (
              <p className="mt-1 flex items-center gap-1 text-xs text-success-500">
                <Eye className="h-3.5 w-3.5" /> Viewed by recruiter
              </p>
            )}
          </div>
          <StatusBadge status={application.status} />
        </div>
      </Card>
    </Link>
  );
}

/** Memoized: pure presentational component rendered in lists (dashboard, applications page). */
export const ApplicationCard = memo(ApplicationCardComponent);
