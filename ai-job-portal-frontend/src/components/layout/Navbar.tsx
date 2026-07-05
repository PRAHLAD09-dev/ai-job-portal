import { Search } from "lucide-react";
import { ThemeSwitch } from "@/components/layout/ThemeSwitch";
import { NotificationBell } from "@/components/layout/NotificationBell";
import { UserMenu } from "@/components/layout/UserMenu";

/** Top navbar shared by all authenticated layouts (01_UI_DESIGN.md: Sidebar + Top Navbar). */
export function Navbar() {
  return (
    <header className="flex h-16 items-center justify-between gap-4 border-b border-[hsl(var(--border-color))] bg-[hsl(var(--surface))] px-4 md:px-6">
      <div className="relative hidden max-w-sm flex-1 md:block">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[hsl(var(--muted))]" />
        <input
          type="search"
          placeholder="Search jobs, candidates, companies..."
          aria-label="Global search"
          className="h-9 w-full rounded-lg border border-[hsl(var(--border-color))] bg-[hsl(var(--background))] pl-9 pr-3 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500"
        />
      </div>
      <div className="ml-auto flex items-center gap-2 md:ml-0">
        <ThemeSwitch />
        <NotificationBell />
        <UserMenu />
      </div>
    </header>
  );
}
