package com.prahlad.aijobportal.recruiterservice.company.entity;

import com.prahlad.aijobportal.recruiterservice.company.enums.CompanySize;
import com.prahlad.aijobportal.recruiterservice.company.enums.Industry;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import com.prahlad.aijobportal.recruiterservice.config.BaseEntity;
import com.prahlad.aijobportal.recruiterservice.location.entity.CompanyLocation;
import com.prahlad.aijobportal.recruiterservice.recruiter.entity.Recruiter;
import com.prahlad.aijobportal.recruiterservice.sociallink.entity.CompanySocialLink;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A recruiting organization. Owns ONLY company-profile concerns — per
 * PROJECT_SPECIFICATION.md Section 18 (Module Boundaries), the Recruiter
 * Service must NOT manage candidate profile or authentication data.
 *
 * One {@code Recruiter} is the company's creator/owner; additional
 * recruiters may belong to the same company (many recruiters per
 * company), modeled as a one-to-many from {@code Company} to
 * {@code Recruiter}.
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"recruiters", "locations", "socialLinks"})
public class Company extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 220)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry", nullable = false, length = 50)
    private Industry industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_size", nullable = false, length = 30)
    private CompanySize companySize;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "logo_url", length = 1000)
    private String logoUrl;

    @Column(name = "logo_cloudinary_public_id", length = 500)
    private String logoCloudinaryPublicId;

    @Column(name = "banner_url", length = 1000)
    private String bannerUrl;

    @Column(name = "banner_cloudinary_public_id", length = 500)
    private String bannerCloudinaryPublicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "active_job_count", nullable = false)
    @Builder.Default
    private int activeJobCount = 0;

    @Column(name = "total_hires", nullable = false)
    @Builder.Default
    private int totalHires = 0;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Recruiter> recruiters = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CompanyLocation> locations = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CompanySocialLink> socialLinks = new ArrayList<>();
}
