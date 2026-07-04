package com.prahlad.aijobportal.notificationservice.notification.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class NotificationNotFoundException extends ResourceNotFoundException {

    public NotificationNotFoundException(UUID notificationId) {
        super("Notification", "id", notificationId);
    }
}
