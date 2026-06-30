package com.prahlad.aijobportal.recruiterservice.location.dto.response;

import java.util.UUID;

public record CompanyLocationResponse(
        UUID id,
        String addressLine,
        String city,
        String state,
        String country,
        String postalCode,
        boolean headquarters
) {
}
