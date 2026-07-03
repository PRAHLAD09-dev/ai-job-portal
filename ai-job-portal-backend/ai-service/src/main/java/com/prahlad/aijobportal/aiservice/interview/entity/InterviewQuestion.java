package com.prahlad.aijobportal.aiservice.interview.entity;

import com.prahlad.aijobportal.aiservice.config.BaseEntity;
import com.prahlad.aijobportal.aiservice.interview.enums.QuestionCategory;
import com.prahlad.aijobportal.aiservice.interview.enums.QuestionDifficulty;
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
 * Persisted AI-generated interview question for a job, per
 * DAY07_AI_SERVICE.md's "InterviewQuestion" entity. Persisting these
 * (rather than regenerating on every request) lets a recruiter build
 * up a growing, reusable question bank per job instead of getting a
 * different set each time they open the feature.
 */
@Entity
@Table(name = "interview_questions",
        indexes = @Index(name = "idx_interview_question_job", columnList = "job_id"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class InterviewQuestion extends BaseEntity {

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private QuestionDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private QuestionCategory category;
}
