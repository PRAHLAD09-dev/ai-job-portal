package com.prahlad.aijobportal.aiservice.feign.dto;

import java.util.UUID;

public record RecruiterSummaryResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        UUID companyId,
        String companyName
) {
}
