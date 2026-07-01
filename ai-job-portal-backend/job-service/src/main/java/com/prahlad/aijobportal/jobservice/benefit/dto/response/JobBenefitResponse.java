package com.prahlad.aijobportal.jobservice.benefit.dto.response;

import java.util.UUID;

public record JobBenefitResponse(
        UUID id,
        String title,
        String description
) {
}
