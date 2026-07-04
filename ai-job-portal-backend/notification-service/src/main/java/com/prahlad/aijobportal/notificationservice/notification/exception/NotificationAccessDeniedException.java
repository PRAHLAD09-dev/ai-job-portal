package com.prahlad.aijobportal.notificationservice.notification.exception;

import com.prahlad.aijobportal.common.exception.AccessDeniedBusinessException;

public class NotificationAccessDeniedException extends AccessDeniedBusinessException {

    public NotificationAccessDeniedException() {
        super("You do not have permission to access this notification");
    }
}
