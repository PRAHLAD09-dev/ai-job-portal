package com.prahlad.aijobportal.authservice.admin.service.impl;

import com.prahlad.aijobportal.authservice.admin.dto.response.AdminUserResponse;
import com.prahlad.aijobportal.authservice.admin.dto.response.UserPlatformStatisticsResponse;
import com.prahlad.aijobportal.authservice.admin.mapper.AdminUserMapper;
import com.prahlad.aijobportal.authservice.admin.service.AdminUserService;
import com.prahlad.aijobportal.authservice.admin.specification.AdminUserSpecification;
import com.prahlad.aijobportal.authservice.user.entity.User;
import com.prahlad.aijobportal.authservice.user.enums.AccountStatus;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import com.prahlad.aijobportal.authservice.user.repository.UserRepository;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AdminUserMapper adminUserMapper;

    @Override
    public Page<AdminUserResponse> searchUsers(String keyword, RoleName role, AccountStatus status, Pageable pageable) {
        return userRepository.findAll(AdminUserSpecification.withCriteria(keyword, role, status), pageable)
                .map(adminUserMapper::toResponse);
    }

    @Override
    public AdminUserResponse getUser(UUID userId) {
        User user = findUserOrThrow(userId);
        return adminUserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse enableUser(UUID userId) {
        User user = findUserOrThrow(userId);
        user.setStatus(AccountStatus.ACTIVE);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        log.info("Admin action: user {} enabled", userId);
        return adminUserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse disableUser(UUID userId) {
        User user = findUserOrThrow(userId);
        user.setStatus(AccountStatus.DISABLED);
        log.info("Admin action: user {} disabled", userId);
        return adminUserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = findUserOrThrow(userId);
        userRepository.delete(user);
        log.info("Admin action: user {} deleted", userId);
    }

    @Override
    public UserPlatformStatisticsResponse getPlatformStatistics() {
        return new UserPlatformStatisticsResponse(
                userRepository.count(),
                userRepository.countByRoles_Name(RoleName.CANDIDATE),
                userRepository.countByRoles_Name(RoleName.RECRUITER),
                userRepository.countByRoles_Name(RoleName.ADMIN) + userRepository.countByRoles_Name(RoleName.SUPER_ADMIN),
                userRepository.countByStatus(AccountStatus.ACTIVE),
                userRepository.countByStatus(AccountStatus.DISABLED),
                userRepository.countByStatus(AccountStatus.PENDING_VERIFICATION)
        );
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
