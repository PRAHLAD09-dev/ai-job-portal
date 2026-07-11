package com.prahlad.aijobportal.candidateservice.candidate.service;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.repository.CandidateRepository;
import com.prahlad.aijobportal.candidateservice.education.repository.EducationRepository;
import com.prahlad.aijobportal.candidateservice.event.dto.CandidateProfileUpdatedEvent;
import com.prahlad.aijobportal.candidateservice.experience.repository.ExperienceRepository;
import com.prahlad.aijobportal.candidateservice.resume.repository.ResumeRepository;
import com.prahlad.aijobportal.candidateservice.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

/**
 * Computes and persists a candidate's profile completion percentage.
 * Recalculated whenever the profile itself or any of its sub-resources
 * (education, experience, skills, resumes) change, so the stored value
 * is always current without the caller needing to know the scoring
 * weights. Also publishes {@link CandidateProfileUpdatedEvent} on every
 * recalculation so downstream consumers can react to profile changes.
 *
 * Weighting (out of 100): basic info 30, headline/summary 15,
 * at least one education entry 15, at least one experience entry 15,
 * at least one skill 15, at least one active resume 10.
 *
 * NOTE: "has at least one X" is checked via existsByCandidateId(...) on
 * each sub-resource repository rather than candidate.getEducations().isEmpty()
 * (etc). The entity's collections are FetchType.LAZY, so calling
 * .isEmpty() on four collections forced four full lazy-load SELECTs on
 * every single recalculation - which runs after nearly every profile,
 * education, experience, skill, and resume mutation. existsBy... issues
 * the same lightweight existence check without ever materializing the
 * collections.
 */
@Service
@RequiredArgsConstructor
public class ProfileCompletionService {

    private final CandidateRepository candidateRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void recalculate(Candidate candidate) {
        int score = 0;

        if (hasBasicInfo(candidate)) {
            score += 30;
        }
        if (StringUtils.hasText(candidate.getHeadline()) && StringUtils.hasText(candidate.getSummary())) {
            score += 15;
        }
        if (educationRepository.existsByCandidateId(candidate.getId())) {
            score += 15;
        }
        if (experienceRepository.existsByCandidateId(candidate.getId())) {
            score += 15;
        }
        if (skillRepository.existsByCandidateId(candidate.getId())) {
            score += 15;
        }
        if (resumeRepository.existsByCandidateId(candidate.getId())) {
            score += 10;
        }

        candidate.setProfileCompletionPercentage(score);
        candidateRepository.save(candidate);

        applicationEventPublisher.publishEvent(new CandidateProfileUpdatedEvent(
                candidate.getId(), candidate.getUserId(), score, Instant.now()
        ));
    }

    private boolean hasBasicInfo(Candidate candidate) {
        return StringUtils.hasText(candidate.getFullName())
                && StringUtils.hasText(candidate.getEmail())
                && StringUtils.hasText(candidate.getPhoneNumber())
                && StringUtils.hasText(candidate.getCity())
                && StringUtils.hasText(candidate.getCountry());
    }
}
