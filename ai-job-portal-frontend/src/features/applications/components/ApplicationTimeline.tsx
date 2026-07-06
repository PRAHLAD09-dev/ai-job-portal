import { CheckCircle2 } from "lucide-react";
import { StatusBadge } from "@/features/applications/components/StatusBadge";
import type { TimelineResponse } from "@/features/applications/types";

/** Visual vertical timeline of every status transition, per DAY02s Application Timeline spec. */
export function ApplicationTimeline({ events }: { events: TimelineResponse[] }) {
  const sorted = [...events].sort((a, b) => new Date(a.changedAt).getTime() - new Date(b.changedAt).getTime());

  return (
    <ol className="relative space-y-6 border-l border-[hsl(var(--border-color))] pl-6">
      {sorted.map((event) => (
        <li key={event.id} className="relative">
          <span className="absolute -left-[1.65rem] top-0.5 flex h-4 w-4 items-center justify-center rounded-full bg-primary-600">
            <CheckCircle2 className="h-4 w-4 text-white" />
          </span>
          <div className="flex flex-wrap items-center gap-2">
            <StatusBadge status={event.newStatus} />
            <span className="text-xs text-[hsl(var(--muted))]">
              {new Date(event.changedAt).toLocaleString()}
            </span>
          </div>
          {event.remarks && <p className="mt-1 text-sm text-[hsl(var(--muted))]">{event.remarks}</p>}
        </li>
      ))}
    </ol>
  );
}
