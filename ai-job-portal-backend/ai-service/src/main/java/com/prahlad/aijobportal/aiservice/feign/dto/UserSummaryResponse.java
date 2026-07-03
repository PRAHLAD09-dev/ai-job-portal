package com.prahlad.aijobportal.aiservice.feign.dto;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String email,
        String firstName,
        String lastName
) {
}
