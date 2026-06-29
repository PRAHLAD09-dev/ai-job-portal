package com.prahlad.aijobportal.candidateservice.experience.entity;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.config.BaseEntity;
import com.prahlad.aijobportal.candidateservice.experience.enums.EmploymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * A single work-experience entry belonging to a {@link Candidate}.
 */
@Entity
@Table(name = "experiences")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"candidate"})
public class Experience extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 30)
    private EmploymentType employmentType;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "currently_working", nullable = false)
    private boolean currentlyWorking;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
