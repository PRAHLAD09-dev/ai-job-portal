package com.prahlad.aijobportal.jobservice.admin.specification;

import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builds a dynamic {@link Specification} for {@link Job}, used
 * exclusively by Admin Service's internal job-management endpoints
 * ({@code GET /api/v1/jobs/internal/admin/jobs}), mirroring the same
 * additive specification-builder pattern already used by
 * {@code JobSpecification} for public job search. Unlike the public
 * specification, this one does NOT constrain results to
 * {@code PUBLISHED} jobs — admins must see jobs in every status.
 * Read-only: never mutates data.
 */
public final class AdminJobSpecification {

    private AdminJobSpecification() {
    }

    public static Specification<Job> withCriteria(String keyword, JobStatus status, UUID companyId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), likePattern);
                Predicate companyMatch = cb.like(cb.lower(root.get("companyName")), likePattern);
                predicates.add(cb.or(titleMatch, companyMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (companyId != null) {
                predicates.add(cb.equal(root.get("companyId"), companyId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
