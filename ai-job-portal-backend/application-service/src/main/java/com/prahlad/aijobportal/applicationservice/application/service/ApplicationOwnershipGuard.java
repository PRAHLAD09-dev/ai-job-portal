package com.prahlad.aijobportal.applicationservice.application.service;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.exception.ApplicationAccessDeniedException;
import com.prahlad.aijobportal.applicationservice.application.exception.ApplicationNotFoundException;
import com.prahlad.aijobportal.applicationservice.application.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Centralizes the "a recruiter may only manage their own company
 * applications" business rule (DAY06_APPLICATION_SERVICE.md's Security
 * section), so every recruiter-side application operation enforces the
 * same ownership check exactly once.
 */
@Service
@RequiredArgsConstructor
public class ApplicationOwnershipGuard {

    private final JobApplicationRepository applicationRepository;

    /**
     * Resolves an application by id and verifies it belongs to the
     * given company, throwing {@link ApplicationAccessDeniedException}
     * if the application exists but belongs to another company (rather
     * than a generic not-found, since the recruiter is authenticated
     * and the distinction matters for them), or
     * {@link ApplicationNotFoundException} if it simply does not exist.
     */
    public JobApplication getOwnedApplicationOrThrow(UUID applicationId, UUID companyId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        if (!application.getCompanyId().equals(companyId)) {
            throw new ApplicationAccessDeniedException("You do not have permission to manage this application");
        }

        return application;
    }
}
