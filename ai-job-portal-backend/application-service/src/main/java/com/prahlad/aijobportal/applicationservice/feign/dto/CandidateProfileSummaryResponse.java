package com.prahlad.aijobportal.applicationservice.feign.dto;

import java.util.List;
import java.util.UUID;

/**
 * Minimal projection of Candidate Service's {@code CandidateProfileResponse},
 * as returned (wrapped in {@code ApiResponse}) by
 * {@code GET /api/v1/candidate/profile} (the caller's OWN profile —
 * Candidate Service exposes no cross-candidate lookup). Used to
 * resolve the applicant's profile id, display name, and resume
 * selection when applying for a job.
 */
public record CandidateProfileSummaryResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        List<ResumeSummaryResponse> resumes
) {

    public record ResumeSummaryResponse(
            UUID id,
            String fileName,
            String fileUrl
    ) {
    }
}
