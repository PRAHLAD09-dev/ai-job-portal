package com.prahlad.aijobportal.jobservice.job.specification;

import com.prahlad.aijobportal.jobservice.job.dto.request.JobSearchCriteria;
import com.prahlad.aijobportal.jobservice.job.entity.Job;
import com.prahlad.aijobportal.jobservice.job.enums.JobStatus;
import com.prahlad.aijobportal.jobservice.location.entity.JobLocation;
import com.prahlad.aijobportal.jobservice.skill.entity.JobSkill;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a dynamic {@link Specification} for {@link Job} from optional
 * {@link JobSearchCriteria} fields (per DAY05's Search/Filtering
 * sections), combining only the predicates for criteria that were
 * actually supplied. Always additionally constrains results to
 * {@code PUBLISHED} jobs for public search/browse endpoints, per
 * PROJECT_SPECIFICATION.md Section 16: "Only published jobs are visible
 * to candidates."
 */
public final class JobSpecification {

    private JobSpecification() {
    }

    public static Specification<Job> withCriteria(JobSearchCriteria criteria, boolean publicOnly) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (publicOnly) {
                predicates.add(cb.equal(root.get("status"), JobStatus.PUBLISHED));
            }

            if (criteria == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            if (StringUtils.hasText(criteria.keyword())) {
                String likePattern = "%" + criteria.keyword().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), likePattern);
                Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), likePattern);
                Predicate companyMatch = cb.like(cb.lower(root.get("companyName")), likePattern);
                predicates.add(cb.or(titleMatch, descriptionMatch, companyMatch));
            }

            if (criteria.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), criteria.categoryId()));
            }

            if (criteria.companyId() != null) {
                predicates.add(cb.equal(root.get("companyId"), criteria.companyId()));
            }

            if (criteria.jobType() != null) {
                predicates.add(cb.equal(root.get("jobType"), criteria.jobType()));
            }

            if (criteria.experienceLevel() != null) {
                predicates.add(cb.equal(root.get("experienceLevel"), criteria.experienceLevel()));
            }

            if (criteria.workMode() != null) {
                predicates.add(cb.equal(root.get("workMode"), criteria.workMode()));
            }

            if (criteria.minSalary() != null) {
                predicates.add(cb.or(
                        cb.isNull(root.get("maxSalary")),
                        cb.greaterThanOrEqualTo(root.get("maxSalary"), criteria.minSalary())
                ));
            }

            if (criteria.maxSalary() != null) {
                predicates.add(cb.or(
                        cb.isNull(root.get("minSalary")),
                        cb.lessThanOrEqualTo(root.get("minSalary"), criteria.maxSalary())
                ));
            }

            if (criteria.postedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publishedAt"), criteria.postedAfter()));
            }

            if (StringUtils.hasText(criteria.skill())) {
                Join<Job, JobSkill> skillJoin = root.join("skills");
                predicates.add(cb.equal(cb.lower(skillJoin.get("name")), criteria.skill().toLowerCase()));
                query.distinct(true);
            }

            if (StringUtils.hasText(criteria.city()) || StringUtils.hasText(criteria.state())
                    || StringUtils.hasText(criteria.country())) {
                Join<Job, JobLocation> locationJoin = root.join("locations");
                if (StringUtils.hasText(criteria.city())) {
                    predicates.add(cb.equal(cb.lower(locationJoin.get("city")), criteria.city().toLowerCase()));
                }
                if (StringUtils.hasText(criteria.state())) {
                    predicates.add(cb.equal(cb.lower(locationJoin.get("state")), criteria.state().toLowerCase()));
                }
                if (StringUtils.hasText(criteria.country())) {
                    predicates.add(cb.equal(cb.lower(locationJoin.get("country")), criteria.country().toLowerCase()));
                }
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
