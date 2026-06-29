package com.prahlad.aijobportal.candidateservice.candidate.entity;

import com.prahlad.aijobportal.candidateservice.candidate.enums.ProfileVisibility;
import com.prahlad.aijobportal.candidateservice.config.BaseEntity;
import com.prahlad.aijobportal.candidateservice.education.entity.Education;
import com.prahlad.aijobportal.candidateservice.experience.entity.Experience;
import com.prahlad.aijobportal.candidateservice.resume.entity.Resume;
import com.prahlad.aijobportal.candidateservice.skill.entity.Skill;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The candidate's profile. Owns ONLY candidate-profile concerns — per
 * PROJECT_SPECIFICATION.md Section 18 (Module Boundaries), the Candidate
 * Service must NOT manage authentication, jobs, or applications.
 *
 * Linked to the Auth Service's {@code User} record purely by the
 * {@code userId} foreign key value (no JPA relationship/FK constraint,
 * since cross-service direct database access is forbidden per
 * DECISIONS.md). Authoritative identity/credential data — email, name,
 * password — lives exclusively in Auth Service; this entity only mirrors
 * the denormalized {@code email}/{@code fullName} needed for display and
 * search, fetched once via Feign at profile-creation time.
 */
@Entity
@Table(name = "candidates")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"educations", "experiences", "skills", "resumes"})
public class Candidate extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "headline", length = 200)
    private String headline;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    @Builder.Default
    private ProfileVisibility visibility = ProfileVisibility.PUBLIC;

    @Column(name = "profile_completion_percentage", nullable = false)
    @Builder.Default
    private int profileCompletionPercentage = 0;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Resume> resumes = new ArrayList<>();
}
