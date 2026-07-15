package com.prahlad.aijobportal.aiservice.skillgap.dto.response;

import java.util.List;

/**
 * Buckets the missing skills identified in a {@link SkillGapResponse}
 * by how urgently the candidate should close each gap, per
 * DAY10_AI_Enhancement_ATS_Intelligence.md's "Skill Gap Analysis"
 * section. A skill named here is expected to also appear in
 * {@code missingSkills} - this is a priority view over the same set,
 * not an independent list.
 */
public record SkillPriorityOrderResponse(
        List<String> highPriority,
        List<String> mediumPriority,
        List<String> lowPriority
) {
}
