package com.prahlad.aijobportal.candidateservice.candidate.service;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.repository.CandidateRepository;
import com.prahlad.aijobportal.candidateservice.event.CandidateEventPublisher;
import com.prahlad.aijobportal.candidateservice.event.dto.CandidateProfileUpdatedEvent;
import lombok.RequiredArgsConstructor;
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
 */
@Service
@RequiredArgsConstructor
public class ProfileCompletionService {

    private final CandidateRepository candidateRepository;
    private final CandidateEventPublisher candidateEventPublisher;

    @Transactional
    public void recalculate(Candidate candidate) {
        int score = 0;

        if (hasBasicInfo(candidate)) {
            score += 30;
        }
        if (StringUtils.hasText(candidate.getHeadline()) && StringUtils.hasText(candidate.getSummary())) {
            score += 15;
        }
        if (!candidate.getEducations().isEmpty()) {
            score += 15;
        }
        if (!candidate.getExperiences().isEmpty()) {
            score += 15;
        }
        if (!candidate.getSkills().isEmpty()) {
            score += 15;
        }
        if (!candidate.getResumes().isEmpty()) {
            score += 10;
        }

        candidate.setProfileCompletionPercentage(score);
        candidateRepository.save(candidate);

        candidateEventPublisher.publishCandidateProfileUpdated(new CandidateProfileUpdatedEvent(
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
