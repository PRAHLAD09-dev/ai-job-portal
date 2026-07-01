package com.prahlad.aijobportal.jobservice.job.dto.request;

import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Encapsulates every optional search/filter parameter accepted by
 * {@code GET /jobs} and {@code GET /jobs/search} (per DAY05's Search
 * and Filtering sections), bound from individual {@code @RequestParam}
 * values in the controller rather than a request body — these are GET
 * endpoints. All fields are optional; {@code null} means "no filter on
 * this dimension".
 */
public record JobSearchCriteria(
        String keyword,
        UUID categoryId,
        String skill,
        String city,
        String state,
        String country,
        UUID companyId,
        JobType jobType,
        ExperienceLevel experienceLevel,
        WorkMode workMode,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        Instant postedAfter
) {
}
