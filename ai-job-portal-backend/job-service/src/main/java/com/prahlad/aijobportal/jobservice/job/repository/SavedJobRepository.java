package com.prahlad.aijobportal.jobservice.job.repository;

import com.prahlad.aijobportal.jobservice.job.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SavedJobRepository extends JpaRepository<SavedJob, UUID> {

    Page<SavedJob> findByUserId(UUID userId, Pageable pageable);

    Optional<SavedJob> findByUserIdAndJobId(UUID userId, UUID jobId);

    boolean existsByUserIdAndJobId(UUID userId, UUID jobId);
}
