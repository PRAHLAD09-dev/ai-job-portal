import { NavLink } from "react-router-dom";
import { ChevronsLeft, ChevronsRight } from "lucide-react";
import { Logo } from "@/components/common/Logo";
import type { NavItem } from "@/constants/nav-config";
import { cn } from "@/lib/cn";

interface SidebarProps {
  items: NavItem[];
  collapsed: boolean;
  onToggle: () => void;
}

/** Desktop sidebar; collapsible on tablet per 01_UI_DESIGN.md layout spec. */
export function Sidebar({ items, collapsed, onToggle }: SidebarProps) {
  return (
    <aside
      className={cn(
        "hidden shrink-0 flex-col border-r border-[hsl(var(--border-color))] bg-[hsl(var(--surface))] transition-all duration-200 md:flex",
        collapsed ? "w-16" : "w-64",
      )}
    >
      <div className="flex h-16 items-center justify-between px-4">
        {!collapsed && <Logo />}
        <button
          type="button"
          onClick={onToggle}
          aria-label={collapsed ? "Expand sidebar" : "Collapse sidebar"}
          className="rounded-md p-1.5 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
        >
          {collapsed ? <ChevronsRight className="h-4 w-4" /> : <ChevronsLeft className="h-4 w-4" />}
        </button>
      </div>
      <nav className="flex-1 space-y-1 px-2 py-2">
        {items.map(({ label, path, icon: Icon }) => (
          <NavLink
            key={path}
            to={path}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                isActive
                  ? "bg-primary-600 text-white"
                  : "text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40 hover:text-[hsl(var(--foreground))]",
              )
            }
            title={collapsed ? label : undefined}
          >
            <Icon className="h-4.5 w-4.5 shrink-0" />
            {!collapsed && <span>{label}</span>}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
