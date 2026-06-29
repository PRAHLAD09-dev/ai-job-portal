package com.prahlad.aijobportal.candidateservice.experience.dto.response;

import com.prahlad.aijobportal.candidateservice.experience.enums.EmploymentType;

import java.time.LocalDate;
import java.util.UUID;

public record ExperienceResponse(
        UUID id,
        String companyName,
        String jobTitle,
        EmploymentType employmentType,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentlyWorking,
        String description
) {
}
