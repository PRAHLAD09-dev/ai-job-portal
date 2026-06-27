package com.prahlad.aijobportal.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Auth Service — owns Authentication, Authorization, Users, Roles, JWT
 * issuance/validation, and Refresh Tokens for the AI Job Portal platform.
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} (the
 * shared group package) rather than just this service's own package, so
 * that shared beans from the {@code common} module — such as
 * {@code CorrelationIdFilter} — are picked up automatically without each
 * service needing to redeclare them.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
