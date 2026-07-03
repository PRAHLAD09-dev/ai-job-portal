package com.prahlad.aijobportal.aiservice.recommendation.entity;

import com.prahlad.aijobportal.aiservice.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
 * Persisted AI-generated job match for a candidate, per
 * DAY07_AI_SERVICE.md's "JobRecommendation" entity. One row per
 * (candidateId, jobId) pair; regenerating recommendations replaces
 * the candidate's prior rows for jobs no longer recommended and
 * upserts the rest (see {@code RecommendationServiceImpl}).
 */
@Entity
@Table(name = "job_recommendations",
        uniqueConstraints = @UniqueConstraint(name = "uk_job_recommendation_candidate_job", columnNames = {"candidate_id", "job_id"}),
        indexes = @Index(name = "idx_job_recommendation_candidate", columnList = "candidate_id"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class JobRecommendation extends BaseEntity {

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "match_score", nullable = false)
    private Integer matchScore;

    @Column(name = "reasoning", columnDefinition = "TEXT")
    private String reasoning;
}
