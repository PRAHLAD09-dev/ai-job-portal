package com.prahlad.aijobportal.adminservice.auditlog.specification;

import com.prahlad.aijobportal.adminservice.auditlog.entity.AuditLog;
import com.prahlad.aijobportal.adminservice.auditlog.enums.AuditActionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class AuditLogSpecification {

    private AuditLogSpecification() {
    }

    public static Specification<AuditLog> byCategory(AuditActionType.AuditCategory category, UUID adminId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            List<AuditActionType> actionTypes = Arrays.stream(AuditActionType.values())
                    .filter(type -> type.category() == category)
                    .toList();
            predicates.add(root.get("actionType").in(actionTypes));

            if (adminId != null) {
                predicates.add(cb.equal(root.get("adminId"), adminId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
