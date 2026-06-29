package com.prahlad.aijobportal.candidateservice.education.dto.request;

import com.prahlad.aijobportal.candidateservice.education.enums.DegreeType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EducationRequest(

        @NotBlank(message = "Institution name is required")
        @Size(max = 200, message = "Institution name must not exceed 200 characters")
        String institutionName,

        @NotNull(message = "Degree type is required")
        DegreeType degreeType,

        @NotBlank(message = "Field of study is required")
        @Size(max = 150, message = "Field of study must not exceed 150 characters")
        String fieldOfStudy,

        @NotNull(message = "Start date is required")
        @PastOrPresent(message = "Start date cannot be in the future")
        LocalDate startDate,

        LocalDate endDate,

        boolean currentlyStudying,

        @Size(max = 50, message = "Grade must not exceed 50 characters")
        String grade,

        @Size(max = 4000, message = "Description must not exceed 4000 characters")
        String description
) {
    @AssertTrue(message = "End date must be provided when not currently studying, and omitted when currently studying")
    public boolean isEndDateConsistent() {
        return currentlyStudying ? endDate == null : endDate != null;
    }
}
