package com.prahlad.aijobportal.aiservice.interviewprep.dto.response;

import java.util.List;

/**
 * Topics detected from the authenticated candidate's latest resume,
 * per the AI Interview Generator PRD's "Detected Topics" chips step.
 * {@code skills} covers technologies/skills found in the resume;
 * {@code projects} covers project names. The frontend renders both as
 * one combined, selectable chip list - kept separate here only so the
 * caller can label/group them differently if desired.
 */
public record DetectedTopicsResponse(
        List<String> skills,
        List<String> projects
) {
}
