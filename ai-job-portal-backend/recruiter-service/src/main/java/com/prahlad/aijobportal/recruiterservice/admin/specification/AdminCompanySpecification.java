package com.prahlad.aijobportal.recruiterservice.admin.specification;

import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a dynamic {@link Specification} for {@link Company}, used
 * exclusively by Admin Service's internal company-management endpoints
 * ({@code GET /api/v1/companies/internal/admin/companies}), mirroring the
 * same additive specification-builder pattern already used by Job
 * Service's {@code JobSpecification}. Read-only: never mutates data.
 */
public final class AdminCompanySpecification {

    private AdminCompanySpecification() {
    }

    public static Specification<Company> withCriteria(String keyword, VerificationStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), likePattern);
                Predicate slugMatch = cb.like(cb.lower(root.get("slug")), likePattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), likePattern);
                predicates.add(cb.or(nameMatch, slugMatch, emailMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("verificationStatus"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
