package com.prahlad.aijobportal.jobservice.job.dto.response;

import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobAlertFrequency;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;

import java.util.UUID;

public record JobAlertResponse(
        UUID id,
        String keyword,
        UUID categoryId,
        JobType jobType,
        ExperienceLevel experienceLevel,
        WorkMode workMode,
        String city,
        JobAlertFrequency frequency,
        boolean active
) {
}
