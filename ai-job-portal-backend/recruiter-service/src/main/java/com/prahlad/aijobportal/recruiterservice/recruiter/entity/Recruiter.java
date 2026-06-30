package com.prahlad.aijobportal.recruiterservice.recruiter.entity;

import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.config.BaseEntity;
import com.prahlad.aijobportal.recruiterservice.recruiter.enums.RecruiterTitle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * A recruiter's profile, linked to the Auth Service's {@code User}
 * record purely by the {@code userId} value (no JPA relationship/FK
 * constraint, since cross-service direct database access is forbidden
 * per DECISIONS.md). Authoritative identity data — email, name,
 * password — lives exclusively in Auth Service; this entity only
 * mirrors the denormalized {@code email}/{@code fullName} fetched once
 * via Feign at recruiter-profile-creation time.
 *
 * Exactly one {@code Recruiter} owns the {@code Company} they created
 * ({@code isOwner = true}); additional recruiters may later be invited
 * to the same company (a feature reserved for a future phase) — the
 * many-to-one relationship already supports that without further
 * schema changes.
 */
@Entity
@Table(name = "recruiters")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"company"})
public class Recruiter extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "title", nullable = false, length = 50)
    private RecruiterTitle title;

    @Column(name = "designation", length = 150)
    private String designation;

    @Column(name = "profile_picture_url", length = 1000)
    private String profilePictureUrl;

    @Column(name = "is_owner", nullable = false)
    @Builder.Default
    private boolean owner = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
