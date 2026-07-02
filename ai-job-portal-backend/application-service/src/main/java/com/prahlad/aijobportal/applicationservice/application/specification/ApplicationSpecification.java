package com.prahlad.aijobportal.applicationservice.application.specification;

import com.prahlad.aijobportal.applicationservice.application.dto.request.ApplicationSearchCriteria;
import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builds a dynamic {@link Specification} for {@link JobApplication}
 * from a company scope plus optional {@link ApplicationSearchCriteria}
 * fields, combining only the predicates for criteria that were
 * actually supplied. Always constrains results to the recruiter's own
 * {@code companyId}, per DAY06's Security rule ("Recruiter can manage
 * only company applications").
 */
public final class ApplicationSpecification {

    private ApplicationSpecification() {
    }

    public static Specification<JobApplication> forCompany(UUID companyId, ApplicationSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("companyId"), companyId));

            if (criteria == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (StringUtils.hasText(criteria.keyword())) {
                String likePattern = "%" + criteria.keyword().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("candidateName")), likePattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("candidateEmail")), likePattern);
                Predicate jobMatch = cb.like(cb.lower(root.get("jobTitle")), likePattern);
                predicates.add(cb.or(nameMatch, emailMatch, jobMatch));
            }

            if (criteria.jobId() != null) {
                predicates.add(cb.equal(root.get("jobId"), criteria.jobId()));
            }

            if (criteria.status() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.status()));
            }

            if (criteria.appliedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("appliedAt"), criteria.appliedAfter()));
            }

            if (criteria.appliedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("appliedAt"), criteria.appliedBefore()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
