package com.prahlad.aijobportal.jobservice.requirement.enums;

/**
 * Distinguishes the kind of bullet-point text a {@code JobRequirement}
 * row represents, since job postings typically separate qualifications
 * from day-to-day responsibilities.
 */
public enum RequirementType {
    QUALIFICATION,
    RESPONSIBILITY,
    NICE_TO_HAVE
}
