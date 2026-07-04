package com.prahlad.aijobportal.notificationservice.notification.service;

import com.prahlad.aijobportal.notificationservice.notification.dto.request.NotificationPreferenceRequest;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationPreferenceResponse;
import com.prahlad.aijobportal.notificationservice.notification.entity.NotificationPreference;

import java.util.UUID;

public interface NotificationPreferenceService {

    NotificationPreferenceResponse getMyPreferences(UUID userId);

    NotificationPreferenceResponse updateMyPreferences(UUID userId, NotificationPreferenceRequest request);

    /** Used internally by NotificationServiceImpl to gate delivery channels; creates a default row on first use. */
    NotificationPreference getOrCreateEntity(UUID userId);
}
