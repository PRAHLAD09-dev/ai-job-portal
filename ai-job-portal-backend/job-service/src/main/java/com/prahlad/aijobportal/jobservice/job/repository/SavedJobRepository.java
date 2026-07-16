package com.prahlad.aijobportal.jobservice.job.repository;

import com.prahlad.aijobportal.jobservice.job.entity.SavedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedJobRepository extends JpaRepository<SavedJob, UUID> {

    Page<SavedJob> findByUserId(UUID userId, Pageable pageable);

    Optional<SavedJob> findByUserIdAndJobId(UUID userId, UUID jobId);

    boolean existsByUserIdAndJobId(UUID userId, UUID jobId);

    /**
     * DAY11 Recruiter Dashboard "Saved Job Statistics": how many
     * candidates have bookmarked each of a company's jobs. Grouped by
     * job so the dashboard gets every job's count in one query instead
     * of N+1 calls.
     */
    @Query("select sj.job.id as jobId, sj.job.title as jobTitle, count(sj) as savedCount "
            + "from SavedJob sj where sj.job.companyId = :companyId group by sj.job.id, sj.job.title")
    List<JobSavedCountProjection> countSavedByCompanyGroupedByJob(@Param("companyId") UUID companyId);

    interface JobSavedCountProjection {
        UUID getJobId();
        String getJobTitle();
        long getSavedCount();
    }
}
