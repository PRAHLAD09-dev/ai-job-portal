package com.prahlad.aijobportal.aiservice.resumeanalysis.entity;

import com.prahlad.aijobportal.aiservice.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Persisted result of a Gemini-driven resume analysis, per
 * DAY07_AI_SERVICE.md's "ResumeAnalysis" entity. One row per completed
 * analysis (a candidate re-analyzing the same resume text creates a
 * new row rather than overwriting the previous one, preserving
 * history), keyed for lookup by {@code candidateId} + a hash of
 * {@code resumeText} so "No duplicate analysis" (DAY07_AI_SERVICE.md
 * Validation) can be enforced without re-calling Gemini.
 *
 * List-typed fields ({@code strengths}, {@code weaknesses},
 * {@code missingSkills}, {@code recommendations}) are stored as a
 * single delimited TEXT column rather than a child table — this is
 * AI-generated, read-mostly, always-loaded-together content, not a
 * relationally-queried collection, so a join table would add
 * complexity with no benefit.
 */
@Entity
@Table(name = "resume_analysis",
        indexes = {
                @Index(name = "idx_resume_analysis_candidate", columnList = "candidate_id"),
                @Index(name = "idx_resume_analysis_resume_hash", columnList = "candidate_id, resume_text_hash")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = "resumeText")
public class ResumeAnalysis extends BaseEntity {

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "resume_url", nullable = false, length = 1000)
    private String resumeUrl;

    @Column(name = "resume_text", nullable = false, columnDefinition = "TEXT")
    private String resumeText;

    /** SHA-256 of {@code resumeText}, used to detect a duplicate analysis request cheaply. */
    @Column(name = "resume_text_hash", nullable = false, length = 64)
    private String resumeTextHash;

    @Column(name = "ats_score", nullable = false)
    private Integer atsScore;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
}
