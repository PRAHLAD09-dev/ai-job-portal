package com.prahlad.aijobportal.adminservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Admin Service — owns ONLY platform administration for the AI Job
 * Portal platform: dashboard statistics, user/company/job moderation,
 * application/AI/notification monitoring, and audit logs. Every
 * cross-service read or moderation action is performed via OpenFeign
 * against Auth, Recruiter, Job, Application, AI, and Notification
 * Service's internal-only admin endpoints — this service never
 * duplicates their business logic or accesses their databases directly.
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} so
 * shared beans from the {@code common} module (e.g.
 * {@code CorrelationIdFilter}) are picked up automatically, mirroring
 * the same approach used in every other service.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
@EnableFeignClients
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
