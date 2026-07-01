package com.prahlad.aijobportal.jobservice.job.repository;

import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * {@link JpaSpecificationExecutor} enables dynamic keyword/category/skill/
 * location/salary filtering and sorting (per DAY05's Search/Filtering/
 * Sorting requirements) without hand-writing a combinatorial explosion
 * of derived query methods.
 */
public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {

    Optional<Job> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Optional<Job> findByIdAndCompanyId(UUID id, UUID companyId);

    Page<Job> findByCompanyId(UUID companyId, Pageable pageable);

    Page<Job> findByStatusOrderByPublishedAtDesc(JobStatus status, Pageable pageable);

    Page<Job> findByStatusAndFeaturedTrueOrderByPublishedAtDesc(JobStatus status, Pageable pageable);

    long countByCompanyId(UUID companyId);

    long countByCompanyIdAndStatus(UUID companyId, JobStatus status);

    @Modifying
    @Query("update Job j set j.viewCount = j.viewCount + 1 where j.id = :jobId")
    int incrementViewCount(@Param("jobId") UUID jobId);
}
