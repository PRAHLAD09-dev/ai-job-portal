package com.prahlad.aijobportal.aiservice.learningroadmap.dto.response;

import java.util.List;

/**
 * AI-generated learning path for a candidate, per
 * DAY10_AI_Enhancement_ATS_Intelligence.md's "Learning Roadmap"
 * section. Stateless and always recomputed from the candidate's
 * current skills and the live job market sample (same shape as
 * {@code SkillGapResponse}) rather than persisted, since a roadmap is
 * only ever "current advice", not a record that needs history.
 */
public record LearningRoadmapResponse(
        List<String> beginnerTopics,
        List<String> intermediateTopics,
        List<String> advancedTopics,
        List<String> suggestedResources,
        List<String> practiceOrder
) {
}
