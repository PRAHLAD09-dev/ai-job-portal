import { Moon, Sun, Monitor } from "lucide-react";
import { useTheme } from "@/hooks/useTheme";
import { cn } from "@/lib/cn";

const OPTIONS = [
  { value: "light", icon: Sun, label: "Light theme" },
  { value: "dark", icon: Moon, label: "Dark theme" },
  { value: "system", icon: Monitor, label: "System theme" },
] as const;

export function ThemeSwitch() {
  const { theme, setTheme } = useTheme();
  return (
    <div className="flex items-center rounded-lg border border-[hsl(var(--border-color))] p-0.5">
      {OPTIONS.map(({ value, icon: Icon, label }) => (
        <button
          key={value}
          type="button"
          aria-label={label}
          aria-pressed={theme === value}
          onClick={() => setTheme(value)}
          className={cn(
            "rounded-md p-1.5 transition-colors",
            theme === value
              ? "bg-primary-600 text-white"
              : "text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/50",
          )}
        >
          <Icon className="h-4 w-4" />
        </button>
      ))}
    </div>
  );
}
