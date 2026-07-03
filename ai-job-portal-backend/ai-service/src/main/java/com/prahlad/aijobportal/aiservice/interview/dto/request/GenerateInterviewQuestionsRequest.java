package com.prahlad.aijobportal.aiservice.interview.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateInterviewQuestionsRequest(

        @NotNull(message = "Job ID is required")
        java.util.UUID jobId,

        @Min(value = 1, message = "Count must be at least 1")
        @Max(value = 20, message = "Count must not exceed 20")
        Integer count
) {
    public int countOrDefault() {
        return count == null ? 10 : count;
    }
}
