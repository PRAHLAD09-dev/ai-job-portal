package com.prahlad.aijobportal.aiservice.skillgap.dto.response;

import java.util.List;

public record SkillGapResponse(
        List<String> currentSkills,
        List<String> missingSkills,
        List<String> careerSuggestions,
        SkillPriorityOrderResponse priorityOrder,
        List<String> learningSuggestions
) {
}
