import { Bell } from "lucide-react";

/** Bell icon with unread badge — full notification center wired up in Phase 4. */
export function NotificationBell({ unreadCount = 0 }: { unreadCount?: number }) {
  return (
    <button
      type="button"
      aria-label={`Notifications${unreadCount ? `, ${unreadCount} unread` : ""}`}
      className="relative rounded-lg p-2 text-[hsl(var(--muted))] hover:bg-[hsl(var(--border-color))]/40"
    >
      <Bell className="h-5 w-5" />
      {unreadCount > 0 && (
        <span className="absolute right-1 top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-danger-500 px-1 text-[10px] font-semibold text-white">
          {unreadCount > 9 ? "9+" : unreadCount}
        </span>
      )}
    </button>
  );
}
