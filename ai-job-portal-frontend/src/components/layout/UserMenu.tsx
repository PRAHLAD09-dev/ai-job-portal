import { useState, useRef, useEffect } from "react";
import { LogOut, Settings, User as UserIcon } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { useLogout } from "@/features/auth/hooks/useAuthMutations";
import { tokenStorage } from "@/lib/storage";
import { ROUTES } from "@/constants/routes";

export function UserMenu() {
  const { user } = useAuth();
  const logout = useLogout();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const onClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("mousedown", onClick);
    return () => document.removeEventListener("mousedown", onClick);
  }, []);

  if (!user) return null;
  const initials = `${user.firstName[0] ?? ""}${user.lastName[0] ?? ""}`.toUpperCase();

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        onClick={() => setOpen((o) => !o)}
        className="flex h-9 w-9 items-center justify-center rounded-full bg-primary-600 text-sm font-semibold text-white"
        aria-haspopup="menu"
        aria-expanded={open}
      >
        {initials}
      </button>
      {open && (
        <div
          role="menu"
          className="absolute right-0 mt-2 w-52 rounded-lg border border-[hsl(var(--border-color))] bg-[hsl(var(--surface))] p-1 shadow-lg"
        >
          <div className="px-3 py-2 text-xs text-[hsl(var(--muted))]">
            <p className="truncate font-medium text-[hsl(var(--foreground))]">
              {user.firstName} {user.lastName}
            </p>
            <p className="truncate">{user.email}</p>
          </div>
          <hr className="my-1 border-[hsl(var(--border-color))]" />
          <button
            role="menuitem"
            className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
            onClick={() => {
              setOpen(false);
              navigate(ROUTES.CANDIDATE_PROFILE);
            }}
          >
            <UserIcon className="h-4 w-4" /> Profile
          </button>
          <button
            role="menuitem"
            className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm hover:bg-[hsl(var(--border-color))]/40"
            onClick={() => {
              setOpen(false);
              navigate(ROUTES.CANDIDATE_SETTINGS);
            }}
          >
            <Settings className="h-4 w-4" /> Settings
          </button>
          <button
            role="menuitem"
            className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm text-danger-500 hover:bg-[hsl(var(--border-color))]/40"
            onClick={() => logout.mutate(tokenStorage.getRefreshToken() ?? "")}
          >
            <LogOut className="h-4 w-4" /> Logout
          </button>
        </div>
      )}
    </div>
  );
}
