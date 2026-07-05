import { cn } from "@/lib/cn";
import { ENV } from "@/constants/env";

/** Simple geometric "AI" mark, flat + SVG, dark/light compatible (01_UI_DESIGN.md). */
export function Logo({ className, showName = true }: { className?: string; showName?: boolean }) {
  return (
    <div className={cn("flex items-center gap-2", className)}>
      <svg width="28" height="28" viewBox="0 0 32 32" fill="none" aria-hidden="true">
        <rect width="32" height="32" rx="8" className="fill-primary-600" />
        <path
          d="M9 21L14 9H17.5L22.5 21H19.4L18.4 18.4H13L12 21H9ZM13.9 15.9H17.5L15.7 11.3L13.9 15.9Z"
          fill="white"
        />
      </svg>
      {showName && <span className="text-base font-semibold tracking-tight">{ENV.APP_NAME}</span>}
    </div>
  );
}
