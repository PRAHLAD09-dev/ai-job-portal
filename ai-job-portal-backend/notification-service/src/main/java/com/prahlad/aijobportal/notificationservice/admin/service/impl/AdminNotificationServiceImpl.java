package com.prahlad.aijobportal.notificationservice.admin.service.impl;

import com.prahlad.aijobportal.notificationservice.admin.dto.response.NotificationPlatformStatisticsResponse;
import com.prahlad.aijobportal.notificationservice.admin.service.AdminNotificationService;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationStatus;
import com.prahlad.aijobportal.notificationservice.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationPlatformStatisticsResponse getPlatformStatistics() {
        return new NotificationPlatformStatisticsResponse(
                notificationRepository.count(),
                notificationRepository.countByStatus(NotificationStatus.PENDING),
                notificationRepository.countByStatus(NotificationStatus.SENT),
                notificationRepository.countByStatus(NotificationStatus.FAILED),
                notificationRepository.countByReadTrue(),
                notificationRepository.countByReadFalse()
        );
    }
}
