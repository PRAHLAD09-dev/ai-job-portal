import { NavLink } from "react-router-dom";
import type { NavItem } from "@/constants/nav-config";
import { cn } from "@/lib/cn";

/** Bottom navigation bar for mobile, per 01_UI_DESIGN.md layout spec. */
export function MobileNav({ items }: { items: NavItem[] }) {
  const visible = items.slice(0, 5);
  return (
    <nav className="fixed inset-x-0 bottom-0 z-40 flex border-t border-[hsl(var(--border-color))] bg-[hsl(var(--surface))] md:hidden">
      {visible.map(({ label, path, icon: Icon }) => (
        <NavLink
          key={path}
          to={path}
          className={({ isActive }) =>
            cn(
              "flex flex-1 flex-col items-center gap-0.5 py-2 text-[10px] font-medium",
              isActive ? "text-primary-600" : "text-[hsl(var(--muted))]",
            )
          }
        >
          <Icon className="h-5 w-5" />
          {label}
        </NavLink>
      ))}
    </nav>
  );
}
