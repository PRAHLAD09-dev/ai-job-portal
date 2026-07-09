import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import {
  notificationPreferenceService,
  notificationService,
} from "@/features/notifications/services/notification.service";
import { extractErrorMessage } from "@/services/api-client";
import { useAuth } from "@/hooks/useAuth";
import type { NotificationPreferenceRequest } from "@/features/notifications/types";

export const NOTIFICATIONS_QUERY_KEY = ["notifications"] as const;
export const NOTIFICATIONS_UNREAD_COUNT_KEY = ["notifications", "unread-count"] as const;
export const NOTIFICATIONS_LATEST_KEY = ["notifications", "latest"] as const;
export const NOTIFICATION_PREFERENCES_KEY = ["notifications", "preferences"] as const;

/** Polls unread count every 30s so the bell badge stays fresh without a websocket. */
export function useUnreadNotificationCount() {
  const { isAuthenticated } = useAuth();
  return useQuery({
    queryKey: NOTIFICATIONS_UNREAD_COUNT_KEY,
    queryFn: () => notificationService.getUnreadCount().then((res) => res.data.count),
    enabled: isAuthenticated,
    refetchInterval: 30_000,
  });
}

export function useLatestNotifications(enabled: boolean) {
  return useQuery({
    queryKey: NOTIFICATIONS_LATEST_KEY,
    queryFn: () => notificationService.getLatest().then((res) => res.data),
    enabled,
  });
}

export function useNotificationsList(params: { page: number; size: number }) {
  return useQuery({
    queryKey: [...NOTIFICATIONS_QUERY_KEY, "list", params],
    queryFn: () => notificationService.getMyNotifications(params).then((res) => res.data),
    placeholderData: (previousData) => previousData,
  });
}

function invalidateNotifications(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: NOTIFICATIONS_QUERY_KEY });
  queryClient.invalidateQueries({ queryKey: NOTIFICATIONS_UNREAD_COUNT_KEY });
  queryClient.invalidateQueries({ queryKey: NOTIFICATIONS_LATEST_KEY });
}

export function useMarkNotificationRead() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (notificationId: string) => notificationService.markAsRead(notificationId),
    onSuccess: () => invalidateNotifications(queryClient),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useMarkAllNotificationsRead() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => notificationService.markAllAsRead(),
    onSuccess: (response) => {
      invalidateNotifications(queryClient);
      toast.success(response.message || "All notifications marked as read");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useDeleteNotification() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (notificationId: string) => notificationService.remove(notificationId),
    onSuccess: () => invalidateNotifications(queryClient),
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}

export function useNotificationPreferences() {
  return useQuery({
    queryKey: NOTIFICATION_PREFERENCES_KEY,
    queryFn: () => notificationPreferenceService.getMine().then((res) => res.data),
  });
}

export function useUpdateNotificationPreferences() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: NotificationPreferenceRequest) => notificationPreferenceService.update(payload),
    onSuccess: (response) => {
      queryClient.setQueryData(NOTIFICATION_PREFERENCES_KEY, response.data);
      toast.success(response.message || "Preferences updated");
    },
    onError: (error) => toast.error(extractErrorMessage(error)),
  });
}
