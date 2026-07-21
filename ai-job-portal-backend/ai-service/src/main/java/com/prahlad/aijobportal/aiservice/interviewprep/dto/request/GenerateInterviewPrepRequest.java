package com.prahlad.aijobportal.aiservice.interviewprep.dto.request;

import com.prahlad.aijobportal.aiservice.interviewprep.enums.PrepDifficulty;
import com.prahlad.aijobportal.aiservice.interviewprep.enums.PrepQuestionType;
import jakarta.validation.constraints.NotEmpty;

import java.util.Comparator;
import java.util.List;

/**
 * Request to generate a fresh set of resume-based interview practice
 * questions for the authenticated candidate, per the AI Interview
 * Generator PRD's Steps 3-6. {@code selectedTopics} is expected to be a
 * subset of (or, for "Select All", equal to) the topics previously
 * returned by {@code GET /ai/interview-prep/topics} - the frontend is
 * expected to call that endpoint first; this endpoint does not
 * re-derive topics itself, it only uses the caller's selection to focus
 * the prompt.
 */
public record GenerateInterviewPrepRequest(

        @NotEmpty(message = "Select at least one topic to generate questions for")
        List<String> selectedTopics,

        PrepDifficulty difficulty,

        Integer questionCount,

        PrepQuestionType questionType
) {
    /** Per the PRD's "Question Count" step: only 10, 20, or 30 are offered. */
    private static final List<Integer> ALLOWED_COUNTS = List.of(10, 20, 30);

    public PrepDifficulty difficultyOrDefault() {
        return difficulty == null ? PrepDifficulty.MEDIUM : difficulty;
    }

    public PrepQuestionType questionTypeOrDefault() {
        return questionType == null ? PrepQuestionType.MIXED : questionType;
    }

    /**
     * Defaults to 20 (per the PRD) when not supplied. If a value is
     * supplied that isn't exactly one of the three offered options, it
     * is snapped to the nearest allowed value rather than rejected -
     * the three counts are a UI convenience, not a business rule worth
     * a hard validation failure over.
     */
    public int countOrDefault() {
        if (questionCount == null) {
            return 20;
        }
        return ALLOWED_COUNTS.stream()
                .min(Comparator.comparingInt(allowed -> Math.abs(allowed - questionCount)))
                .orElse(20);
    }
}
