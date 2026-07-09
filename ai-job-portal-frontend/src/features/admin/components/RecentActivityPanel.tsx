import { Activity } from "lucide-react";
import { Card } from "@/components/ui/card";
import { EmptyState } from "@/components/common/EmptyState";
import { formatEnumLabel } from "@/utils/format";
import type { AuditLogResponse } from "@/features/admin/types";

function timeAgo(dateString: string) {
  const seconds = Math.floor((Date.now() - new Date(dateString).getTime()) / 1000);
  if (seconds < 60) return "just now";
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  return `${Math.floor(hours / 24)}d ago`;
}

export function RecentActivityPanel({ activity }: { activity: AuditLogResponse[] }) {
  return (
    <Card>
      <h2 className="text-lg font-semibold">Recent activity</h2>
      {activity.length === 0 ? (
        <div className="mt-4">
          <EmptyState icon={<Activity className="h-10 w-10" />} title="No records found" message="No admin activity yet." />
        </div>
      ) : (
        <div className="mt-4 space-y-3">
          {activity.map((entry) => (
            <div key={entry.id} className="flex items-start justify-between gap-3 border-b border-[hsl(var(--border-color))] pb-3 last:border-b-0 last:pb-0">
              <div>
                <p className="text-sm">{entry.description}</p>
                <p className="mt-0.5 text-xs text-[hsl(var(--muted))]">
                  {entry.adminEmail} · {formatEnumLabel(entry.actionType)}
                </p>
              </div>
              <span className="shrink-0 text-xs text-[hsl(var(--muted))]">{timeAgo(entry.createdAt)}</span>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
