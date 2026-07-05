import type { LucideIcon } from "lucide-react";
import {
  LayoutDashboard,
  User,
  Briefcase,
  FileText,
  Sparkles,
  Bell,
  Settings,
  Building2,
  Users,
  BarChart3,
} from "lucide-react";
import { ROUTES } from "@/constants/routes";

export interface NavItem {
  label: string;
  path: string;
  icon: LucideIcon;
}

// Mirrors the Navigation section of 01_UI_DESIGN.md exactly, per role.
export const CANDIDATE_NAV: NavItem[] = [
  { label: "Dashboard", path: ROUTES.CANDIDATE_DASHBOARD, icon: LayoutDashboard },
  { label: "Jobs", path: ROUTES.CANDIDATE_JOBS, icon: Briefcase },
  { label: "Applications", path: ROUTES.CANDIDATE_APPLICATIONS, icon: FileText },
  { label: "AI", path: ROUTES.CANDIDATE_AI, icon: Sparkles },
  { label: "Profile", path: ROUTES.CANDIDATE_PROFILE, icon: User },
  { label: "Settings", path: ROUTES.CANDIDATE_SETTINGS, icon: Settings },
];

export const RECRUITER_NAV: NavItem[] = [
  { label: "Dashboard", path: ROUTES.RECRUITER_DASHBOARD, icon: LayoutDashboard },
  { label: "Company", path: ROUTES.RECRUITER_COMPANY, icon: Building2 },
  { label: "Jobs", path: ROUTES.RECRUITER_JOBS, icon: Briefcase },
  { label: "Candidates", path: ROUTES.RECRUITER_CANDIDATES, icon: Users },
  { label: "AI", path: ROUTES.RECRUITER_AI, icon: Sparkles },
];

export const ADMIN_NAV: NavItem[] = [
  { label: "Dashboard", path: ROUTES.ADMIN_DASHBOARD, icon: LayoutDashboard },
  { label: "Users", path: ROUTES.ADMIN_USERS, icon: Users },
  { label: "Companies", path: ROUTES.ADMIN_COMPANIES, icon: Building2 },
  { label: "Jobs", path: ROUTES.ADMIN_JOBS, icon: Briefcase },
  { label: "Analytics", path: ROUTES.ADMIN_DASHBOARD, icon: BarChart3 },
];

export const GUEST_NAV: NavItem[] = [
  { label: "Home", path: ROUTES.HOME, icon: LayoutDashboard },
  { label: "Jobs", path: ROUTES.JOBS, icon: Briefcase },
];

export const NOTIFICATIONS_ICON = Bell;
