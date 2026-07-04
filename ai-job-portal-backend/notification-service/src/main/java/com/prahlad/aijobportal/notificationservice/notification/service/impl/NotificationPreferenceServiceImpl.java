package com.prahlad.aijobportal.notificationservice.notification.service.impl;

import com.prahlad.aijobportal.notificationservice.notification.dto.request.NotificationPreferenceRequest;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationPreferenceResponse;
import com.prahlad.aijobportal.notificationservice.notification.entity.NotificationPreference;
import com.prahlad.aijobportal.notificationservice.notification.mapper.NotificationMapper;
import com.prahlad.aijobportal.notificationservice.notification.repository.NotificationPreferenceRepository;
import com.prahlad.aijobportal.notificationservice.notification.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Manages per-user notification delivery preferences. A default row
 * (email + in-app enabled, push disabled — push is design-ready only
 * per DAY08_NOTIFICATION_SERVICE.md Channels section) is created lazily
 * on first access rather than at user registration time, since this
 * service never consumes an event that guarantees firing before every
 * other notification-triggering event.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getMyPreferences(UUID userId) {
        return notificationMapper.toResponse(getOrCreateEntity(userId));
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse updateMyPreferences(UUID userId, NotificationPreferenceRequest request) {
        NotificationPreference preference = getOrCreateEntity(userId);
        preference.setEmailEnabled(request.emailEnabled());
        preference.setPushEnabled(request.pushEnabled());
        preference.setInAppEnabled(request.inAppEnabled());
        preferenceRepository.save(preference);
        log.info("Updated notification preferences for userId={}", userId);
        return notificationMapper.toResponse(preference);
    }

    @Override
    @Transactional
    public NotificationPreference getOrCreateEntity(UUID userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.save(
                        NotificationPreference.builder()
                                .userId(userId)
                                .emailEnabled(true)
                                .pushEnabled(false)
                                .inAppEnabled(true)
                                .build()));
    }
}
