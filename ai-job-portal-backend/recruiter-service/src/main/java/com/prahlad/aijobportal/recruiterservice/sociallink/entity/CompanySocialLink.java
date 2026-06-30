package com.prahlad.aijobportal.recruiterservice.sociallink.entity;

import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.config.BaseEntity;
import com.prahlad.aijobportal.recruiterservice.sociallink.enums.SocialPlatform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * A single social/web presence link belonging to a {@link Company}. A
 * company may not list the same platform twice (enforced at the
 * database level via a unique constraint on {@code company_id + platform}).
 */
@Entity
@Table(name = "company_social_links", uniqueConstraints = {
        @UniqueConstraint(name = "uk_social_links_company_platform", columnNames = {"company_id", "platform"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"company"})
public class CompanySocialLink extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 30)
    private SocialPlatform platform;

    @Column(name = "url", nullable = false, length = 500)
    private String url;
}
