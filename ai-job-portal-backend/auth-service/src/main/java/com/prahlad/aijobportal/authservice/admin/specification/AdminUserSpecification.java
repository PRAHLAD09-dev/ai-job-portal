package com.prahlad.aijobportal.authservice.admin.specification;

import com.prahlad.aijobportal.authservice.user.entity.Role;
import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a dynamic {@link Specification} for {@link User}, used exclusively
 * by Admin Service's internal user-management endpoints
 * ({@code GET /api/v1/auth/internal/admin/users}), mirroring the same
 * additive specification-builder pattern already used by Job Service's
 * {@code JobSpecification}. Read-only: never mutates data.
 */
public final class AdminUserSpecification {

    private AdminUserSpecification() {
    }

    public static Specification<User> withCriteria(String keyword, RoleName role, AccountStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), likePattern);
                Predicate firstNameMatch = cb.like(cb.lower(root.get("firstName")), likePattern);
                Predicate lastNameMatch = cb.like(cb.lower(root.get("lastName")), likePattern);
                predicates.add(cb.or(emailMatch, firstNameMatch, lastNameMatch));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (role != null) {
                Join<User, Role> roleJoin = root.join("roles");
                predicates.add(cb.equal(roleJoin.get("name"), role));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
