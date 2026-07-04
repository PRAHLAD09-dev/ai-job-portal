package com.prahlad.aijobportal.notificationservice.notification.service;

import com.prahlad.aijobportal.common.response.PageResponse;
import com.prahlad.aijobportal.notificationservice.email.EmailContent;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationResponse;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    PageResponse<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable);

    List<NotificationResponse> getLatestNotifications(UUID userId);

    long getUnreadCount(UUID userId);

    void markAsRead(UUID userId, UUID notificationId);

    void markAllAsRead(UUID userId);

    void delete(UUID userId, UUID notificationId);

    /**
     * Persists an in-app notification (if the user has in-app delivery
     * enabled) and dispatches an e-mail (if the user has e-mail delivery
     * enabled and an e-mail address can be resolved). Called exclusively
     * by NotificationEventConsumer in reaction to a Kafka domain event —
     * never exposed via a controller.
     */
    void dispatch(UUID userId, NotificationType type, String title, String message, EmailContent emailContent);
}
