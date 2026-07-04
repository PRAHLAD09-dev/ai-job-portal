package com.prahlad.aijobportal.notificationservice.notification.entity;

import com.prahlad.aijobportal.notificationservice.config.BaseEntity;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationStatus;
import com.prahlad.aijobportal.notificationservice.notification.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * A single in-app / email notification record delivered to a user.
 * Owned exclusively by the Notification Service (notification_service_db),
 * per PROJECT_SPECIFICATION.md Section 18 (Module Boundaries). Populated
 * by KafkaNotificationConsumer reacting to domain events published by
 * every other business microservice.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_user_read", columnList = "user_id, read")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class Notification extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @Column(name = "read", nullable = false)
    @Builder.Default
    private boolean read = false;
}
