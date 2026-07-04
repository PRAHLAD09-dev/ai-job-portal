package com.prahlad.aijobportal.notificationservice.feign.dto;

import java.util.UUID;

/**
 * Local mirror of Auth Service UserResponse (fields this service
 * actually needs). Services never share DTO classes across module
 * boundaries — each consumer declares its own copy of the fields it
 * needs, so a producer-side field addition never breaks this client.
 * Spring Boot's default Jackson configuration disables
 * FAIL_ON_UNKNOWN_PROPERTIES, so extra fields on the producer's actual
 * response (roles, status, emailVerified, createdAt) are safely ignored.
 */
public record UserSummaryResponse(
        UUID id,
        String email,
        String firstName,
        String lastName
) {
}
