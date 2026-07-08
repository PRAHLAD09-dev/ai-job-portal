package com.prahlad.aijobportal.applicationservice.application.repository;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID>, JpaSpecificationExecutor<JobApplication> {

    boolean existsByJobIdAndCandidateId(UUID jobId, UUID candidateId);

    Optional<JobApplication> findByIdAndCandidateUserId(UUID id, UUID candidateUserId);

    Optional<JobApplication> findByIdAndCompanyId(UUID id, UUID companyId);

    Page<JobApplication> findByCandidateUserId(UUID candidateUserId, Pageable pageable);

    List<JobApplication> findByCandidateUserId(UUID candidateUserId);

    Page<JobApplication> findByCompanyId(UUID companyId, Pageable pageable);

    long countByCompanyId(UUID companyId);

    long countByCompanyIdAndStatus(UUID companyId, ApplicationStatus status);

    long countByCandidateUserId(UUID candidateUserId);

    long countByCandidateUserIdAndStatus(UUID candidateUserId, ApplicationStatus status);

    long countByJobId(UUID jobId);

    // ---- Added for Admin Service (DAY09_ADMIN_SERVICE.md) platform
    // application statistics. ----
    long countByStatus(ApplicationStatus status);
}
