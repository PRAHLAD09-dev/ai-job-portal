package com.prahlad.aijobportal.jobservice.job.entity;

import com.prahlad.aijobportal.jobservice.benefit.entity.JobBenefit;
import com.prahlad.aijobportal.jobservice.category.entity.JobCategory;
import com.prahlad.aijobportal.jobservice.config.BaseEntity;
import com.prahlad.aijobportal.jobservice.job.enums.ApplyMethod;
import com.prahlad.aijobportal.jobservice.job.enums.Currency;
import com.prahlad.aijobportal.jobservice.job.enums.ExperienceLevel;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.job.enums.JobType;
import com.prahlad.aijobportal.jobservice.job.enums.SalaryType;
import com.prahlad.aijobportal.jobservice.job.enums.WorkMode;
import com.prahlad.aijobportal.jobservice.location.entity.JobLocation;
import com.prahlad.aijobportal.jobservice.requirement.entity.JobRequirement;
import com.prahlad.aijobportal.jobservice.skill.entity.JobSkill;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A single job posting. Owns ONLY job-listing concerns — per
 * PROJECT_SPECIFICATION.md Section 18 (Module Boundaries), the Job
 * Service must NOT manage applications or interviews.
 *
 * Linked to the Recruiter Service's {@code Company}/{@code Recruiter}
 * records purely by {@code companyId}/{@code recruiterId} VALUE (no JPA
 * relationship/FK constraint, since cross-service direct database
 * access is forbidden per DECISIONS.md). Authoritative company/recruiter
 * data lives exclusively in Recruiter Service; this entity only mirrors
 * the denormalized {@code companyName}/{@code companyLogoUrl} needed
 * for fast listing display without an extra Feign call per job.
 */
@Entity
@Table(name = "jobs")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"skills", "benefits", "locations", "requirements"})
public class Job extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "recruiter_id", nullable = false)
    private UUID recruiterId;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "company_logo_url", length = 1000)
    private String companyLogoUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private JobCategory category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 240)
    private String slug;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 30)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 30)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", nullable = false, length = 20)
    private WorkMode workMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.DRAFT;

    @Column(name = "min_salary", precision = 14, scale = 2)
    private BigDecimal minSalary;

    @Column(name = "max_salary", precision = 14, scale = 2)
    private BigDecimal maxSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type", length = 20)
    private SalaryType salaryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 10)
    private Currency currency;

    @Column(name = "vacancies", nullable = false)
    @Builder.Default
    private int vacancies = 1;

    @Column(name = "application_deadline")
    private Instant applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "apply_method", nullable = false, length = 20)
    @Builder.Default
    private ApplyMethod applyMethod = ApplyMethod.EASY_APPLY;

    @Column(name = "external_apply_url", length = 1000)
    private String externalApplyUrl;

    @Column(name = "featured", nullable = false)
    @Builder.Default
    private boolean featured = false;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private long viewCount = 0;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<JobSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<JobBenefit> benefits = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @Builder.Default
    private List<JobLocation> locations = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<JobRequirement> requirements = new ArrayList<>();
}
