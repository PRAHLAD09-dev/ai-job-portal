package com.prahlad.aijobportal.authservice.user.repository;

import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /** DAY12 "Google OAuth" — look an account back up by Google's stable "sub" claim. */
    Optional<User> findByGoogleId(String googleId);

    /**
     * DAY12 "Admin Dashboard: User Growth" — real daily signup counts
     * since {@code since}, one row per day that had at least one signup.
     * A native query (rather than JPQL) because {@code date_trunc} is
     * PostgreSQL-specific; this project only ever targets Postgres (see
     * every other migration/repository in this service).
     */
    @Query(value = """
            SELECT date_trunc('day', created_at)::date AS day, COUNT(*) AS signupCount
            FROM users
            WHERE created_at >= :since
            GROUP BY day
            ORDER BY day
            """, nativeQuery = true)
    List<UserGrowthProjection> findDailySignupCountsSince(@Param("since") Instant since);

    // ---- Added for Admin Service (DAY09_ADMIN_SERVICE.md) platform
    // statistics. JpaSpecificationExecutor above (also additive) powers the
    // admin list/search/filter endpoint. ----
    long countByStatus(AccountStatus status);

    long countByRoles_Name(RoleName roleName);

    // ---- Added for AdminBootstrapRunner: lets startup bootstrap check
    // "does any admin-tier user already exist" in a single query, across
    // both ADMIN and SUPER_ADMIN roles. ----
    boolean existsByRoles_NameIn(Collection<RoleName> roleNames);

    /** Row shape returned by {@link #findDailySignupCountsSince}. */
    interface UserGrowthProjection {
        LocalDate getDay();
        long getSignupCount();
    }
}

