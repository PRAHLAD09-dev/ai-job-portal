package com.prahlad.aijobportal.candidateservice.candidate.dto.response;

import com.prahlad.aijobportal.candidateservice.candidate.enums.ProfileVisibility;
import com.prahlad.aijobportal.candidateservice.education.dto.response.EducationResponse;
import com.prahlad.aijobportal.candidateservice.experience.dto.response.ExperienceResponse;
import com.prahlad.aijobportal.candidateservice.resume.dto.response.ResumeResponse;
import com.prahlad.aijobportal.candidateservice.skill.dto.response.SkillResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CandidateProfileResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        String headline,
        String summary,
        String phoneNumber,
        LocalDate dateOfBirth,
        String city,
        String state,
        String country,
        String portfolioUrl,
        String linkedinUrl,
        String githubUrl,
        ProfileVisibility visibility,
        int profileCompletionPercentage,
        List<EducationResponse> educations,
        List<ExperienceResponse> experiences,
        List<SkillResponse> skills,
        List<ResumeResponse> resumes
) {
}
