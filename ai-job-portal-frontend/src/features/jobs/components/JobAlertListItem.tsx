import { Bell, Pencil, Trash2 } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { formatEnumLabel } from "@/utils/format";
import type { JobAlertResponse, JobCategoryResponse } from "@/features/jobs/types";

interface JobAlertListItemProps {
  alert: JobAlertResponse;
  categories: JobCategoryResponse[] | undefined;
  onEdit: (alert: JobAlertResponse) => void;
  onDelete: (alert: JobAlertResponse) => void;
}

export function JobAlertListItem({ alert, categories, onEdit, onDelete }: JobAlertListItemProps) {
  const categoryName = categories?.find((c) => c.id === alert.categoryId)?.name;

  const criteria = [
    alert.keyword,
    categoryName,
    alert.jobType && formatEnumLabel(alert.jobType),
    alert.experienceLevel && formatEnumLabel(alert.experienceLevel),
    alert.workMode && formatEnumLabel(alert.workMode),
    alert.city,
  ].filter(Boolean) as string[];

  return (
    <Card>
      <div className="flex items-start justify-between gap-3">
        <div className="flex min-w-0 items-start gap-3">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary-600/10 text-primary-600">
            <Bell className="h-5 w-5" />
          </div>
          <div className="min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <p className="font-semibold">{alert.keyword || "Any job"}</p>
              <Badge variant={alert.active ? "success" : "outline"}>{alert.active ? "Active" : "Inactive"}</Badge>
            </div>
            <div className="mt-1.5 flex flex-wrap gap-1.5">
              {criteria.length > 0 ? (
                criteria.map((c, i) => (
                  <Badge key={i} variant="outline">
                    {c}
                  </Badge>
                ))
              ) : (
                <span className="text-sm text-[hsl(var(--muted))]">All jobs</span>
              )}
            </div>
            <p className="mt-2 text-xs text-[hsl(var(--muted))]">
              Notified {formatEnumLabel(alert.frequency).toLowerCase()}
            </p>
          </div>
        </div>
        <div className="flex shrink-0 gap-1">
          <Button variant="ghost" size="sm" onClick={() => onEdit(alert)} aria-label="Edit job alert">
            <Pencil className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="sm" onClick={() => onDelete(alert)} aria-label="Delete job alert">
            <Trash2 className="h-4 w-4 text-danger-500" />
          </Button>
        </div>
      </div>
    </Card>
  );
}
