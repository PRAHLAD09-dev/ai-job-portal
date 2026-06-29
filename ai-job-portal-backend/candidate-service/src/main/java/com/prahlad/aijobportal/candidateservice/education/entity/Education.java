package com.prahlad.aijobportal.candidateservice.education.entity;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.config.BaseEntity;
import com.prahlad.aijobportal.candidateservice.education.enums.DegreeType;
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
 * A single education entry (degree/institution) belonging to a
 * {@link Candidate}. Many-to-one toward {@code Candidate}, since a
 * candidate may list multiple education entries.
 */
@Entity
@Table(name = "educations")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"candidate"})
public class Education extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "institution_name", nullable = false, length = 200)
    private String institutionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree_type", nullable = false, length = 30)
    private DegreeType degreeType;

    @Column(name = "field_of_study", nullable = false, length = 150)
    private String fieldOfStudy;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "currently_studying", nullable = false)
    private boolean currentlyStudying;

    @Column(name = "grade", length = 50)
    private String grade;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
