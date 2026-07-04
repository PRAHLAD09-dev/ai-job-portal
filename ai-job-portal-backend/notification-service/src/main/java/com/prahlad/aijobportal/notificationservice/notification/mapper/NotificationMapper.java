package com.prahlad.aijobportal.notificationservice.notification.mapper;

import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationPreferenceResponse;
import com.prahlad.aijobportal.notificationservice.notification.dto.response.NotificationResponse;
import com.prahlad.aijobportal.notificationservice.notification.entity.Notification;
import com.prahlad.aijobportal.notificationservice.notification.entity.NotificationPreference;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);

    NotificationPreferenceResponse toResponse(NotificationPreference preference);
}
