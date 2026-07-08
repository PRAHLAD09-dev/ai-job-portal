package com.prahlad.aijobportal.notificationservice.notification.repository;

import com.prahlad.aijobportal.notificationservice.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadFalse(UUID userId, Pageable pageable);

    long countByUserIdAndReadFalse(UUID userId);

    // ---- Added for Admin Service (DAY09_ADMIN_SERVICE.md) platform
    // notification statistics. ----
    long countByStatus(com.prahlad.aijobportal.notificationservice.notification.enums.NotificationStatus status);

    long countByReadTrue();

    long countByReadFalse();
}
