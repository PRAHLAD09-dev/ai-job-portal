package com.prahlad.aijobportal.jobservice.requirement.dto.response;

import com.prahlad.aijobportal.jobservice.requirement.enums.RequirementType;

import java.util.UUID;

public record JobRequirementResponse(
        UUID id,
        RequirementType type,
        String description,
        int displayOrder
) {
}
