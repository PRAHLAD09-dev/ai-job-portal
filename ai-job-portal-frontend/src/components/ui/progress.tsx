import { cn } from "@/lib/cn";

/** Simple determinate progress bar (e.g. resume upload progress). */
export function Progress({ value, className }: { value: number; className?: string }) {
  const clamped = Math.min(100, Math.max(0, value));
  return (
    <div
      role="progressbar"
      aria-valuenow={clamped}
      aria-valuemin={0}
      aria-valuemax={100}
      className={cn("h-2 w-full overflow-hidden rounded-full bg-[hsl(var(--border-color))]/60", className)}
    >
      <div className="h-full rounded-full bg-primary-600 transition-all duration-300" style={{ width: `${clamped}%` }} />
    </div>
  );
}
