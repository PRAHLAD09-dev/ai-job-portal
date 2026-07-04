package com.prahlad.aijobportal.notificationservice.notification.service.impl;

import com.prahlad.aijobportal.notificationservice.email.EmailContent;
import com.prahlad.aijobportal.notificationservice.email.EmailDispatcher;
import com.prahlad.aijobportal.notificationservice.notification.entity.Notification;
import com.prahlad.aijobportal.notificationservice.notification.entity.NotificationPreference;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationType;
import com.prahlad.aijobportal.notificationservice.notification.exception.NotificationAccessDeniedException;
import com.prahlad.aijobportal.notificationservice.notification.exception.NotificationNotFoundException;
import com.prahlad.aijobportal.notificationservice.notification.mapper.NotificationMapper;
import com.prahlad.aijobportal.notificationservice.notification.mapper.NotificationMapperImpl;
import com.prahlad.aijobportal.notificationservice.notification.repository.NotificationRepository;
import com.prahlad.aijobportal.notificationservice.notification.service.NotificationPreferenceService;
import com.prahlad.aijobportal.notificationservice.redis.NotificationRedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationPreferenceService preferenceService;
    @Mock private NotificationRedisService notificationRedisService;
    @Mock private EmailDispatcher emailDispatcher;

    private final NotificationMapper notificationMapper = new NotificationMapperImpl();

    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UUID notificationId;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(
                notificationRepository, notificationMapper, preferenceService, notificationRedisService, emailDispatcher);

        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        notification = Notification.builder()
                .userId(userId)
                .title("Application submitted")
                .message("Your application has been submitted.")
                .type(NotificationType.APPLICATION)
                .status(com.prahlad.aijobportal.notificationservice.notification.enums.NotificationStatus.SENT)
                .read(false)
                .build();
        notification.setId(notificationId);

        lenient().when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void dispatch_shouldPersistInAppNotification_whenInAppEnabled() {
        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId).emailEnabled(false).pushEnabled(false).inAppEnabled(true).build();
        when(preferenceService.getOrCreateEntity(userId)).thenReturn(preference);

        notificationService.dispatch(userId, NotificationType.APPLICATION, "Title", "Message", null);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(notificationRedisService, times(1)).incrementUnreadCount(userId);
        verify(emailDispatcher, never()).dispatch(any(), any());
    }

    @Test
    void dispatch_shouldSkipPersistence_whenInAppDisabled() {
        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId).emailEnabled(false).pushEnabled(false).inAppEnabled(false).build();
        when(preferenceService.getOrCreateEntity(userId)).thenReturn(preference);

        notificationService.dispatch(userId, NotificationType.APPLICATION, "Title", "Message", null);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void dispatch_shouldSendEmail_whenEmailEnabledAndContentProvided() {
        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId).emailEnabled(true).pushEnabled(false).inAppEnabled(true).build();
        when(preferenceService.getOrCreateEntity(userId)).thenReturn(preference);
        EmailContent emailContent = new EmailContent("Subject", "<p>Body</p>");

        notificationService.dispatch(userId, NotificationType.SYSTEM, "Title", "Message", emailContent);

        verify(emailDispatcher, times(1)).dispatch(userId, emailContent);
    }

    @Test
    void markAsRead_shouldDecrementUnreadCount_whenNotificationBelongsToUser() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(userId, notificationId);

        assertThat(notification.isRead()).isTrue();
        verify(notificationRedisService, times(1)).decrementUnreadCount(userId);
    }

    @Test
    void markAsRead_shouldThrow_whenNotificationBelongsToDifferentUser() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        UUID someoneElse = UUID.randomUUID();

        assertThatThrownBy(() -> notificationService.markAsRead(someoneElse, notificationId))
                .isInstanceOf(NotificationAccessDeniedException.class);
    }

    @Test
    void markAsRead_shouldThrow_whenNotificationDoesNotExist() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(userId, notificationId))
                .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    void getUnreadCount_shouldFallBackToDatabase_whenRedisCacheMisses() {
        when(notificationRedisService.getUnreadCount(userId)).thenReturn(null);
        when(notificationRepository.countByUserIdAndReadFalse(userId)).thenReturn(3L);

        long count = notificationService.getUnreadCount(userId);

        assertThat(count).isEqualTo(3L);
        verify(notificationRedisService, times(1)).resetUnreadCount(userId, 3L);
    }
}
