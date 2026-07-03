package com.prahlad.aijobportal.aiservice.jobdescription.dto.response;

import java.util.List;

public record JobDescriptionResponse(
        String description,
        List<String> requiredSkills
) {
}
