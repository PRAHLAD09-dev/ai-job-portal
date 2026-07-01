package com.prahlad.aijobportal.jobservice.job.dto.request;

import com.prahlad.aijobportal.jobservice.benefit.dto.request.JobBenefitRequest;
import com.prahlad.aijobportal.jobservice.job.enums.Currency;
import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.SalaryType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;
import com.prahlad.aijobportal.jobservice.location.dto.request.JobLocationRequest;
import com.prahlad.aijobportal.jobservice.requirement.dto.request.JobRequirementRequest;
import com.prahlad.aijobportal.jobservice.skill.dto.request.JobSkillRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UpdateJobRequest(

        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 20000, message = "Description must not exceed 20000 characters")
        String description,

        @NotNull(message = "Job type is required")
        JobType jobType,

        @NotNull(message = "Experience level is required")
        ExperienceLevel experienceLevel,

        @NotNull(message = "Work mode is required")
        WorkMode workMode,

        BigDecimal minSalary,

        BigDecimal maxSalary,

        SalaryType salaryType,

        Currency currency,

        @Min(value = 1, message = "Vacancies must be at least 1")
        int vacancies,

        @Future(message = "Application deadline must be in the future")
        Instant applicationDeadline,

        @NotEmpty(message = "At least one location is required")
        @Valid
        List<JobLocationRequest> locations,

        @Valid
        List<JobSkillRequest> skills,

        @Valid
        List<JobBenefitRequest> benefits,

        @Valid
        List<JobRequirementRequest> requirements
) {
    @AssertTrue(message = "Maximum salary must be greater than or equal to minimum salary")
    public boolean isSalaryRangeValid() {
        return minSalary == null || maxSalary == null || maxSalary.compareTo(minSalary) >= 0;
    }
}
