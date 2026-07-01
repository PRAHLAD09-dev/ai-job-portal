package com.prahlad.aijobportal.jobservice.category.dto.response;

import java.util.UUID;

public record JobCategoryResponse(
        UUID id,
        String name,
        String slug
) {
}
