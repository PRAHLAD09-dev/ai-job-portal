package com.prahlad.aijobportal.jobservice.location.dto.response;

import java.util.UUID;

public record JobLocationResponse(
        UUID id,
        String city,
        String state,
        String country
) {
}
