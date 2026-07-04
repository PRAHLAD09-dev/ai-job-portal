package com.prahlad.aijobportal.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Notification Service — owns Email Notifications, In-App Notifications,
 * and Notification Preferences for the AI Job Portal platform. Consumes
 * domain events published by every other business microservice via
 * Kafka; never manages Authentication or unrelated business logic, per
 * PROJECT_SPECIFICATION.md Section 18 (Module Boundaries).
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} so
 * shared beans from the {@code common} module are picked up
 * automatically, mirroring every other business service.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
@EnableFeignClients
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
