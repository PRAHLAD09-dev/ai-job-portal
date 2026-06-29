package com.prahlad.aijobportal.candidateservice.skill.entity;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.config.BaseEntity;
import com.prahlad.aijobportal.candidateservice.skill.enums.SkillProficiency;
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
 * A single skill entry belonging to a {@link Candidate}. A candidate may
 * not list the same skill name twice (enforced at the database level via
 * a unique constraint on {@code candidate_id + name}).
 */
@Entity
@Table(name = "skills", uniqueConstraints = {
        @UniqueConstraint(name = "uk_skills_candidate_name", columnNames = {"candidate_id", "name"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"candidate"})
public class Skill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency", nullable = false, length = 30)
    private SkillProficiency proficiency;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
}
