package com.prahlad.aijobportal.candidateservice.candidate.service;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.exception.CandidateNotFoundException;
import com.prahlad.aijobportal.candidateservice.candidate.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Resolves the {@link Candidate} profile owned by the authenticated user.
 * Shared by Education, Experience, Skill, and Resume services so the
 * "find candidate by userId or throw" lookup is implemented exactly once
 * (PROJECT_RULES.md: no duplicate code) rather than copy-pasted into
 * every sub-feature service.
 */
@Service
@RequiredArgsConstructor
public class CandidateLookupService {

    private final CandidateRepository candidateRepository;

    public Candidate getByUserIdOrThrow(UUID userId) {
        return candidateRepository.findByUserId(userId)
                .orElseThrow(() -> CandidateNotFoundException.forUser(userId));
    }
}
