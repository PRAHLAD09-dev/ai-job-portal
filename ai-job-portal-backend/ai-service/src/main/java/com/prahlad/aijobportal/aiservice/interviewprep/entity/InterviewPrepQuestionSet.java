package com.prahlad.aijobportal.aiservice.interviewprep.entity;

import com.prahlad.aijobportal.aiservice.config.BaseEntity;
import com.prahlad.aijobportal.aiservice.interviewprep.enums.PrepDifficulty;
import com.prahlad.aijobportal.aiservice.interviewprep.enums.PrepQuestionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Persisted result of one "Generate Interview Questions" run for a
 * candidate's resume, per the AI Interview Generator PRD. One row per
 * generation - including each "Regenerate" click - so a candidate can
 * revisit past practice sets rather than only ever seeing the latest;
 * mirrors {@code ResumeAnalysis}'s "new row per run, never overwritten"
 * approach.
 *
 * <p>{@code questionsJson} stores the AI-generated questions, each
 * tagged with the topic/section it belongs to (e.g. "Java",
 * "AI Job Portal"), as a single JSON-array TEXT column rather than a
 * child table - this is AI-generated, read-mostly, always-loaded-together
 * content, not a relationally-queried collection (same rationale
 * {@code ResumeAnalysis} uses for its delimited-text list columns).
 * {@link com.prahlad.aijobportal.aiservice.interviewprep.mapper.InterviewPrepMapper}
 * groups it back into topic sections for the API response.
 */
@Entity
@Table(name = "interview_prep_question_sets",
        indexes = @Index(name = "idx_interview_prep_candidate", columnList = "candidate_id"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = "questionsJson")
public class InterviewPrepQuestionSet extends BaseEntity {

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    /** The {@code ResumeAnalysis} row whose resumeText this set was generated from - kept for traceability only. */
    @Column(name = "resume_analysis_id", nullable = false)
    private UUID resumeAnalysisId;

    @Column(name = "selected_topics", nullable = false, columnDefinition = "TEXT")
    private String selectedTopics;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private PrepDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private PrepQuestionType questionType;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount;

    @Column(name = "questions_json", nullable = false, columnDefinition = "TEXT")
    private String questionsJson;
}
