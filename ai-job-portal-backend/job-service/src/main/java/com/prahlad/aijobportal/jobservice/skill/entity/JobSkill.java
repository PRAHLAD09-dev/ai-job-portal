package com.prahlad.aijobportal.jobservice.skill.entity;

import com.prahlad.aijobportal.jobservice.config.BaseEntity;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.skill.enums.RequiredProficiency;
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
 * A single required/preferred skill for a {@link Job}. A job may not
 * list the same skill name twice (enforced via a unique constraint on
 * {@code job_id + name}).
 */
@Entity
@Table(name = "job_skills", uniqueConstraints = {
        @UniqueConstraint(name = "uk_job_skills_job_name", columnNames = {"job_id", "name"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"job"})
public class JobSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_proficiency", nullable = false, length = 30)
    private RequiredProficiency requiredProficiency;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory;
}
