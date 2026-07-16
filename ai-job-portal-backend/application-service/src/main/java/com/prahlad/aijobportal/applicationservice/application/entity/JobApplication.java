package com.prahlad.aijobportal.applicationservice.application.entity;

import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * A single candidate's application to a job posting. Owns ONLY
 * application/hiring-workflow concerns — per PROJECT_SPECIFICATION.md
 * Section 18 (Module Boundaries), the Application Service must NOT
 * manage Jobs or Candidate Profiles.
 *
 * Linked to the Job Service's {@code Job} and Candidate Service's
 * {@code CandidateProfile}/{@code Resume} records purely by ID VALUE
 * (no JPA relationship/FK constraint, since cross-service direct
 * database access is forbidden per DECISIONS.md). {@code jobTitle},
 * {@code companyName}, and {@code resumeUrl} are denormalized at
 * apply-time (mirroring Job Service's denormalized {@code companyName}
 * on {@code Job}) so listing/detail views never require a live
 * cross-service call.
 *
 * {@code candidateUserId} and {@code recruiterUserId} are the Auth
 * Service user ids of the applicant and the reviewing recruiter
 * respectively, kept alongside the Candidate/Recruiter Service profile
 * ids ({@code candidateId}/{@code recruiterId}) so ownership checks on
 * the candidate side never require an extra Feign call.
 */
@Entity
@Table(name = "applications",
        uniqueConstraints = @UniqueConstraint(name = "uk_application_job_candidate", columnNames = {"job_id", "candidate_id"}),
        indexes = {
                @Index(name = "idx_application_candidate_user", columnList = "candidate_user_id"),
                @Index(name = "idx_application_company", columnList = "company_id"),
                @Index(name = "idx_application_job", columnList = "job_id"),
                @Index(name = "idx_application_status", columnList = "status")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class JobApplication extends BaseEntity {

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "candidate_user_id", nullable = false)
    private UUID candidateUserId;

    @Column(name = "candidate_name", nullable = false, length = 200)
    private String candidateName;

    @Column(name = "candidate_email", nullable = false, length = 200)
    private String candidateEmail;

    @Column(name = "recruiter_id")
    private UUID recruiterId;

    @Column(name = "recruiter_user_id")
    private UUID recruiterUserId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "job_title", nullable = false, length = 200)
    private String jobTitle;

    @Column(name = "resume_url", nullable = false, length = 1000)
    private String resumeUrl;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt;

    @Column(name = "interview_date")
    private Instant interviewDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;

    /**
     * DAY11 "Viewed by Recruiter" tracking. Set exactly once, the first
     * time any recruiter at the owning company opens this application's
     * detail view (see {@code RecruiterApplicationServiceImpl#getApplicationDetail}).
     * {@code viewedBy} stores the Auth Service user id of that recruiter.
     */
    @Column(name = "viewed", nullable = false)
    @Builder.Default
    private boolean viewed = false;

    @Column(name = "viewed_at")
    private Instant viewedAt;

    @Column(name = "viewed_by")
    private UUID viewedBy;
}
