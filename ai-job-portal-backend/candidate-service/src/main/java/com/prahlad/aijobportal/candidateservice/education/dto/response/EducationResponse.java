package com.prahlad.aijobportal.candidateservice.education.dto.response;

import com.prahlad.aijobportal.candidateservice.education.enums.DegreeType;

import java.time.LocalDate;
import java.util.UUID;

public record EducationResponse(
        UUID id,
        String institutionName,
        DegreeType degreeType,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentlyStudying,
        String grade,
        String description
) {
}
