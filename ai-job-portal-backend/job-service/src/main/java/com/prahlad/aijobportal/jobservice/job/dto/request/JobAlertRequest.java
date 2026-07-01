package com.prahlad.aijobportal.jobservice.job.dto.request;

import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobAlertFrequency;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record JobAlertRequest(

        @Size(max = 200, message = "Keyword must not exceed 200 characters")
        String keyword,

        UUID categoryId,

        JobType jobType,

        ExperienceLevel experienceLevel,

        WorkMode workMode,

        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @NotNull(message = "Frequency is required")
        JobAlertFrequency frequency
) {
}
