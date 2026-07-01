package com.prahlad.aijobportal.jobservice.job.entity;

import com.prahlad.aijobportal.jobservice.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.UUID;

/**
 * A candidate's bookmark of a {@link Job}. Linked to the candidate by
 * {@code userId} value only (no FK — cross-service direct database
 * access is forbidden per DECISIONS.md). A candidate may not save the
 * same job twice (enforced via a unique constraint on
 * {@code user_id + job_id}).
 */
@Entity
@Table(name = "saved_jobs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_saved_jobs_user_job", columnNames = {"user_id", "job_id"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"job"})
public class SavedJob extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}
