import { useEffect, useRef, useState } from "react";
import { Bell, Check, CheckCheck } from "lucide-react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { ROUTES } from "@/constants/routes";
import { useAuth } from "@/hooks/useAuth";
import {
  useLatestNotifications,
  useMarkAllNotificationsRead,
  useMarkNotificationRead,
  useUnreadNotificationCount,
} from "@/features/notifications/hooks/useNotifications";

function timeAgo(dateString: string) {
  const seconds = Math.floor((Date.now() - new Date(dateString).getTime()) / 1000);
  if (seconds < 60) return "just now";
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes}m ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours}h ago`;
  return `${Math.floor(hours / 24)}d ago`;
}

const SETTINGS_ROUTE_BY_ROLE: Record<string, string | undefined> = {
  CANDIDATE: ROUTES.CANDIDATE_SETTINGS,
  RECRUITER: ROUTES.RECRUITER_SETTINGS,
};

/** Bell icon with unread badge — opens a live notification drawer backed by NotificationController. */
export function NotificationBell() {
  const { user } = useAuth();
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  const { data: unreadCount = 0 } = useUnreadNotificationCount();
  const { data: notifications, isLoading } = useLatestNotifications(open);
  const markAsRead = useMarkNotificationRead();
  const markAllAsRead = useMarkAllNotificationsRead();

  useEffect(() => {
    const onClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    document.addEventListener("mousedown", onClick);
    return () => document.removeEventListener("mousedown", onClick);
  }, []);

  const primaryRole = user?.roles.find((role) => role in SETTINGS_ROUTE_BY_ROLE);
  const settingsRoute = primaryRole ? SETTINGS_ROUTE_BY_ROLE[primaryRole] : undefined;

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        aria-haspopup="menu"
        aria-expanded={open}
        aria-label={`Notifications${unreadCount ? `, ${unreadCount} unread` : ""}`}
        className="relative rounded-lg p-2 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
        onClick={() => setOpen((o) => !o)}
      >
        <Bell className="h-5 w-5" />
        {unreadCount > 0 && (
          <span className="absolute right-1 top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-danger-500 px-1 text-[10px] font-semibold text-white">
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div
          role="menu"
          className="absolute right-0 z-50 mt-2 w-80 rounded-xl border border-[hsl(var(--border-color))] bg-[hsl(var(--surface))] shadow-lg"
        >
          <div className="flex items-center justify-between border-b border-[hsl(var(--border-color))] px-4 py-3">
            <p className="text-sm font-semibold">Notifications</p>
            {unreadCount > 0 && (
              <Button
                variant="ghost"
                size="sm"
                isLoading={markAllAsRead.isPending}
                onClick={() => markAllAsRead.mutate()}
              >
                <CheckCheck className="h-3.5 w-3.5" /> Mark all read
              </Button>
            )}
          </div>
          <div className="max-h-96 overflow-y-auto">
            {isLoading && (
              <div className="space-y-2 p-4">
                <Skeleton className="h-10 w-full" />
                <Skeleton className="h-10 w-full" />
              </div>
            )}
            {!isLoading && (!notifications || notifications.length === 0) && (
              <p className="p-4 text-center text-sm text-[hsl(var(--muted))]">No data available.</p>
            )}
            {notifications?.map((notification) => (
              <div
                key={notification.id}
                className={`flex items-start justify-between gap-2 border-b border-[hsl(var(--border-color))] px-4 py-3 text-sm last:border-b-0 ${
                  notification.read ? "" : "bg-primary-600/5"
                }`}
              >
                <div>
                  <p className="font-medium">{notification.title}</p>
                  <p className="mt-0.5 text-xs text-[hsl(var(--muted))]">{notification.message}</p>
                  <p className="mt-1 text-[11px] text-[hsl(var(--muted))]">{timeAgo(notification.createdAt)}</p>
                </div>
                {!notification.read && (
                  <button
                    type="button"
                    aria-label="Mark as read"
                    className="mt-0.5 shrink-0 rounded-full p-1 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
                    onClick={() => markAsRead.mutate(notification.id)}
                  >
                    <Check className="h-3.5 w-3.5" />
                  </button>
                )}
              </div>
            ))}
          </div>
          {settingsRoute && (
            <div className="border-t border-[hsl(var(--border-color))] p-2 text-center">
              <Link to={settingsRoute} className="text-xs text-primary-600 hover:underline" onClick={() => setOpen(false)}>
                Notification preferences
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
