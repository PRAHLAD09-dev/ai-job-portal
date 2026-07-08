package com.prahlad.aijobportal.authservice.admin.service;

import com.prahlad.aijobportal.authservice.admin.dto.response.AdminUserResponse;
import com.prahlad.aijobportal.authservice.admin.dto.response.UserPlatformStatisticsResponse;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Backs Auth Service's internal admin endpoints, called exclusively by
 * Admin Service via Feign + the shared internal-service token. Reuses the
 * existing {@code User}/{@code UserRepository} — no new persistence
 * concept and no duplication of {@code AuthService}'s own registration/
 * login business logic.
 */
public interface AdminUserService {

    Page<AdminUserResponse> searchUsers(String keyword, RoleName role, AccountStatus status, Pageable pageable);

    AdminUserResponse getUser(UUID userId);

    AdminUserResponse enableUser(UUID userId);

    AdminUserResponse disableUser(UUID userId);

    void deleteUser(UUID userId);

    UserPlatformStatisticsResponse getPlatformStatistics();
}
