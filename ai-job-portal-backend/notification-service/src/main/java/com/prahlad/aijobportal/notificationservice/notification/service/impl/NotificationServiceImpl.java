package com.prahlad.aijobportal.notificationservice.notification.service.impl;

import com.prahlad.aijobportal.common.response.PageResponse;
import com.prahlad.aijobportal.notificationservice.email.EmailContent;
import com.prahlad.aijobportal.notificationservice.email.EmailDispatcher;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationResponse;
import com.prahlad.aijobportal.notificationservice.notification.entity.Notification;
import com.prahlad.aijobportal.notificationservice.notification.entity.NotificationPreference;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationStatus;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationType;
import com.prahlad.aijobportal.notificationservice.notification.exception.NotificationAccessDeniedException;
import com.prahlad.aijobportal.notificationservice.notification.exception.NotificationNotFoundException;
import com.prahlad.aijobportal.notificationservice.notification.mapper.NotificationMapper;
import com.prahlad.aijobportal.notificationservice.notification.repository.NotificationRepository;
import com.prahlad.aijobportal.notificationservice.notification.service.NotificationPreferenceService;
import com.prahlad.aijobportal.notificationservice.notification.service.NotificationService;
import com.prahlad.aijobportal.notificationservice.redis.NotificationRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Core orchestration for both in-app and e-mail notification delivery.
 * The in-app Notification row is always the source of truth for
 * read/unread state; Redis (unread count + latest notifications) is a
 * best-effort read-optimization maintained alongside it, never instead
 * of it.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationPreferenceService preferenceService;
    private final NotificationRedisService notificationRedisService;
    private final EmailDispatcher emailDispatcher;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(notificationMapper::toResponse));
    }

    @Override
    public List<NotificationResponse> getLatestNotifications(UUID userId) {
        List<NotificationResponse> cached = notificationRedisService.getLatestNotifications(userId);
        if (!cached.isEmpty()) {
            return cached;
        }
        return notificationRepository
                .findByUserId(userId, Pageable.ofSize(10))
                .map(notificationMapper::toResponse)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        Long cached = notificationRedisService.getUnreadCount(userId);
        if (cached != null) {
            return cached;
        }
        long actual = notificationRepository.countByUserIdAndReadFalse(userId);
        notificationRedisService.resetUnreadCount(userId, actual);
        return actual;
    }

    @Override
    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new NotificationAccessDeniedException();
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
            notificationRedisService.decrementUnreadCount(userId);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unread = notificationRepository
                .findByUserIdAndReadFalse(userId, Pageable.unpaged())
                .getContent();

        unread.forEach(notification -> {
            notification.setRead(true);
            notification.setStatus(NotificationStatus.READ);
        });
        notificationRepository.saveAll(unread);
        notificationRedisService.resetUnreadCount(userId, 0);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new NotificationAccessDeniedException();
        }

        boolean wasUnread = !notification.isRead();
        notificationRepository.delete(notification);

        if (wasUnread) {
            notificationRedisService.decrementUnreadCount(userId);
        }
    }

    @Override
    @Transactional
    public void dispatch(UUID userId, NotificationType type, String title, String message, EmailContent emailContent) {
        NotificationPreference preference = preferenceService.getOrCreateEntity(userId);

        if (preference.isInAppEnabled()) {
            recordInApp(userId, type, title, message);
        } else {
            log.debug("In-app notifications disabled for userId={}, skipping persistence", userId);
        }

        if (preference.isEmailEnabled() && emailContent != null) {
            emailDispatcher.dispatch(userId, emailContent);
        }
    }

    private void recordInApp(UUID userId, NotificationType type, String title, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .status(NotificationStatus.SENT)
                .read(false)
                .build();

        notification = notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        notificationRedisService.incrementUnreadCount(userId);
        notificationRedisService.pushLatestNotification(userId, response);

        log.info("Recorded in-app notification [{}] for userId={}", type, userId);
    }

}
