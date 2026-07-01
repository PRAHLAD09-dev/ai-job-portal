package com.prahlad.aijobportal.jobservice.job.dto.response;

import com.prahlad.aijobportal.jobservice.benefit.dto.response.JobBenefitResponse;
import com.prahlad.aijobportal.jobservice.category.dto.response.JobCategoryResponse;
import com.prahlad.aijobportal.jobservice.job.enums.Currency;
import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.SalaryType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;
import com.prahlad.aijobportal.jobservice.location.dto.response.JobLocationResponse;
import com.prahlad.aijobportal.jobservice.requirement.dto.response.JobRequirementResponse;
import com.prahlad.aijobportal.jobservice.skill.dto.response.JobSkillResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Full job detail view, returned by {@code GET /jobs/{id}} and
 * {@code GET /jobs/slug/{slug}}.
 */
public record JobResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String companyLogoUrl,
        JobCategoryResponse category,
        String title,
        String slug,
        String description,
        JobType jobType,
        ExperienceLevel experienceLevel,
        WorkMode workMode,
        JobStatus status,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        SalaryType salaryType,
        Currency currency,
        int vacancies,
        Instant applicationDeadline,
        boolean featured,
        long viewCount,
        Instant publishedAt,
        List<JobLocationResponse> locations,
        List<JobSkillResponse> skills,
        List<JobBenefitResponse> benefits,
        List<JobRequirementResponse> requirements,
        Instant createdAt
) {
}
