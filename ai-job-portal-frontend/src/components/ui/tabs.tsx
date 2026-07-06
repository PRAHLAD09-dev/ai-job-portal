import type { ReactNode } from "react";
import { cn } from "@/lib/cn";

export interface TabItem {
  value: string;
  label: string;
  icon?: ReactNode;
}

interface TabsProps {
  items: TabItem[];
  value: string;
  onChange: (value: string) => void;
  className?: string;
}

/** Simple accessible tab bar; content is rendered by the parent based on `value`. */
export function Tabs({ items, value, onChange, className }: TabsProps) {
  return (
    <div
      role="tablist"
      className={cn(
        "flex gap-1 overflow-x-auto border-b border-[hsl(var(--border-color))]",
        className,
      )}
    >
      {items.map((item) => (
        <button
          key={item.value}
          type="button"
          role="tab"
          aria-selected={value === item.value}
          onClick={() => onChange(item.value)}
          className={cn(
            "flex shrink-0 items-center gap-2 whitespace-nowrap border-b-2 px-3 py-2.5 text-sm font-medium transition-colors",
            value === item.value
              ? "border-primary-600 text-primary-600"
              : "border-transparent text-[hsl(var(--muted))] hover:text-[hsl(var(--foreground))]",
          )}
        >
          {item.icon}
          {item.label}
        </button>
      ))}
    </div>
  );
}
