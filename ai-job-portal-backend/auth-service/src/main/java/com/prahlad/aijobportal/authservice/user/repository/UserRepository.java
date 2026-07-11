package com.prahlad.aijobportal.authservice.user.repository;

import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // ---- Added for Admin Service (DAY09_ADMIN_SERVICE.md) platform
    // statistics. JpaSpecificationExecutor above (also additive) powers the
    // admin list/search/filter endpoint. ----
    long countByStatus(AccountStatus status);

    long countByRoles_Name(RoleName roleName);

    // ---- Added for AdminBootstrapRunner: lets startup bootstrap check
    // "does any admin-tier user already exist" in a single query, across
    // both ADMIN and SUPER_ADMIN roles. ----
    boolean existsByRoles_NameIn(Collection<RoleName> roleNames);
}

