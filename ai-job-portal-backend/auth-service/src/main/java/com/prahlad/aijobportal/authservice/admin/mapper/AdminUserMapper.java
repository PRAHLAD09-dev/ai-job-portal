package com.prahlad.aijobportal.authservice.admin.mapper;

import com.prahlad.aijobportal.authservice.admin.dto.response.AdminUserResponse;
import com.prahlad.aijobportal.authservice.user.entity.Role;
import com.prahlad.aijobportal.authservice.user.entity.User;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    default AdminUserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roleNames,
                user.getStatus(),
                user.isEmailVerified(),
                user.isAccountLocked(),
                user.getFailedLoginAttempts(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
