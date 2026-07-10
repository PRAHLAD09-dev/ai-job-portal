// Mirrors notification-service DTOs/enums exactly. Do not rename fields.

export type NotificationType = "EMAIL" | "SYSTEM" | "JOB" | "APPLICATION" | "INTERVIEW" | "OFFER" | "AI";

export type NotificationStatus = "PENDING" | "SENT" | "FAILED" | "READ";

export interface NotificationResponse {
  id: string;
  userId: string;
  title: string;
  message: string;
  type: NotificationType;
  status: NotificationStatus;
  read: boolean;
  createdAt: string;
}

export interface UnreadCountResponse {
  unreadCount: number;
}

export interface NotificationPreferenceResponse {
  id: string;
  userId: string;
  emailEnabled: boolean;
  pushEnabled: boolean;
  inAppEnabled: boolean;
}

export interface NotificationPreferenceRequest {
  emailEnabled: boolean;
  pushEnabled: boolean;
  inAppEnabled: boolean;
}
