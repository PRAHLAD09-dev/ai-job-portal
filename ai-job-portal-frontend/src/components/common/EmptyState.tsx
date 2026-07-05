import type { ReactNode } from "react";
import { Inbox } from "lucide-react";
import { Button } from "@/components/ui/button";

interface EmptyStateProps {
  icon?: ReactNode;
  title: string;
  message: string;
  actionLabel?: string;
  onAction?: () => void;
}

/** Every module needs: illustration, helpful message, primary action (01_UI_DESIGN.md). */
export function EmptyState({ icon, title, message, actionLabel, onAction }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 rounded-xl border border-dashed border-[hsl(var(--border-color))] py-16 text-center">
      <div className="text-[hsl(var(--muted))]">{icon ?? <Inbox className="h-10 w-10" />}</div>
      <h3 className="text-base font-semibold">{title}</h3>
      <p className="max-w-sm text-sm text-[hsl(var(--muted))]">{message}</p>
      {actionLabel && onAction && (
        <Button size="sm" onClick={onAction} className="mt-2">
          {actionLabel}
        </Button>
      )}
    </div>
  );
}
