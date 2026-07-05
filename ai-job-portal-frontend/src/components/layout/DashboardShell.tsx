import { useState, type ReactNode } from "react";
import { Sidebar } from "@/components/layout/Sidebar";
import { MobileNav } from "@/components/layout/MobileNav";
import { Navbar } from "@/components/layout/Navbar";
import type { NavItem } from "@/constants/nav-config";

export function DashboardShell({ items, children }: { items: NavItem[]; children: ReactNode }) {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <div className="flex min-h-screen bg-[hsl(var(--background))]">
      <Sidebar items={items} collapsed={collapsed} onToggle={() => setCollapsed((c) => !c)} />
      <div className="flex min-h-screen flex-1 flex-col">
        <Navbar />
        <main className="flex-1 overflow-x-hidden p-4 pb-20 md:p-6 md:pb-6">{children}</main>
      </div>
      <MobileNav items={items} />
    </div>
  );
}
