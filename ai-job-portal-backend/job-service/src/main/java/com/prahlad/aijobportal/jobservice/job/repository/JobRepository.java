package com.prahlad.aijobportal.jobservice.job.repository;

import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

/**
 * {@link JpaSpecificationExecutor} enables dynamic keyword/category/skill/
 * location/salary filtering and sorting (per DAY05's Search/Filtering/
 * Sorting requirements) without hand-writing a combinatorial explosion
 * of derived query methods.
 *
 * <p>Every listing query method below is annotated with
 * {@code @EntityGraph(attributePaths = "category")}: {@code Job.category}
 * is {@code @ManyToOne(fetch = LAZY)}, but {@code JobMapper.toSummaryResponse()}
 * reads {@code category.name} for every job in every listing response.
 * Without this, each job in a page triggered its own separate lazy-load
 * query for its category (classic N+1). @EntityGraph on a to-one
 * association is safe to combine with Pageable - unlike a JOIN FETCH on
 * a collection, it can't multiply rows, so pagination/count queries stay
 * correct.
 */
public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {

    Optional<Job> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Optional<Job> findByIdAndCompanyId(UUID id, UUID companyId);

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Job> findAll(Specification<Job> spec, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Job> findByCompanyId(UUID companyId, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Job> findByStatusOrderByPublishedAtDesc(JobStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Job> findByStatusAndFeaturedTrueOrderByPublishedAtDesc(JobStatus status, Pageable pageable);

    long countByCompanyId(UUID companyId);

    long countByCompanyIdAndStatus(UUID companyId, JobStatus status);

    // ---- Added for Admin Service (DAY09_ADMIN_SERVICE.md) platform
    // job statistics. ----
    long countByStatus(JobStatus status);

    long countByFeaturedTrue();

    @Modifying(clearAutomatically = true)
    @Query("update Job j set j.viewCount = j.viewCount + 1 where j.id = :jobId")
    int incrementViewCount(@Param("jobId") UUID jobId);
}
