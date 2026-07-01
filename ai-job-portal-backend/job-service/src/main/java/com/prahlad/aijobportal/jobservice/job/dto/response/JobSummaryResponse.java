package com.prahlad.aijobportal.jobservice.job.dto.response;

import com.prahlad.aijobportal.jobservice.job.enums.Currency;
import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.SalaryType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Lightweight job summary used in list/search/pagination responses,
 * deliberately excluding the full description, benefits, and
 * requirements (fetched only on the detail view) to keep listing
 * payloads small.
 */
public record JobSummaryResponse(
        UUID id,
        String companyName,
        String companyLogoUrl,
        String categoryName,
        String title,
        String slug,
        JobType jobType,
        ExperienceLevel experienceLevel,
        WorkMode workMode,
        JobStatus status,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        SalaryType salaryType,
        Currency currency,
        boolean featured,
        List<String> cities,
        Instant publishedAt
) {
}
