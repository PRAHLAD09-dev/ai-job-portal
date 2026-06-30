package com.prahlad.aijobportal.recruiterservice.recruiter.service;

import com.prahlad.aijobportal.recruiterservice.recruiter.entity.Recruiter;
import com.prahlad.aijobportal.recruiterservice.recruiter.exception.RecruiterNotFoundException;
import com.prahlad.aijobportal.recruiterservice.recruiter.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Resolves the {@link Recruiter} profile owned by the authenticated
 * user. Shared by Company, CompanyLocation, CompanySocialLink, and
 * Company asset services so the "find recruiter by userId or throw"
 * lookup is implemented exactly once (PROJECT_RULES.md: no duplicate
 * code) rather than copy-pasted into every feature service.
 */
@Service
@RequiredArgsConstructor
public class RecruiterLookupService {

    private final RecruiterRepository recruiterRepository;

    public Recruiter getByUserIdOrThrow(UUID userId) {
        return recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> RecruiterNotFoundException.forUser(userId));
    }
}
