import { apiClient } from "@/services/api-client";
import type { ApiResponse, PageResponse } from "@/types/api";
import type {
  NotificationPreferenceRequest,
  NotificationPreferenceResponse,
  NotificationResponse,
  UnreadCountResponse,
} from "@/features/notifications/types";

/** Maps 1:1 to notification-service NotificationController (/notifications). */
export const notificationService = {
  getMyNotifications: (params: { page: number; size: number }) =>
    apiClient
      .get<ApiResponse<PageResponse<NotificationResponse>>>("/notifications", { params })
      .then((res) => res.data),

  getLatest: () =>
    apiClient.get<ApiResponse<NotificationResponse[]>>("/notifications/latest").then((res) => res.data),

  getUnreadCount: () =>
    apiClient.get<ApiResponse<UnreadCountResponse>>("/notifications/unread-count").then((res) => res.data),

  markAsRead: (notificationId: string) =>
    apiClient.patch<ApiResponse<null>>(`/notifications/${notificationId}/read`).then((res) => res.data),

  markAllAsRead: () => apiClient.patch<ApiResponse<null>>("/notifications/read-all").then((res) => res.data),

  remove: (notificationId: string) =>
    apiClient.delete<ApiResponse<null>>(`/notifications/${notificationId}`).then((res) => res.data),
};

/** Maps 1:1 to notification-service NotificationPreferenceController (/notifications/preferences). */
export const notificationPreferenceService = {
  getMine: () =>
    apiClient
      .get<ApiResponse<NotificationPreferenceResponse>>("/notifications/preferences")
      .then((res) => res.data),

  update: (payload: NotificationPreferenceRequest) =>
    apiClient
      .put<ApiResponse<NotificationPreferenceResponse>>("/notifications/preferences", payload)
      .then((res) => res.data),
};
