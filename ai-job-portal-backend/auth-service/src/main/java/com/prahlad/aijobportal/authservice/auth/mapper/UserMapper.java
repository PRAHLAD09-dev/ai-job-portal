package com.prahlad.aijobportal.authservice.auth.mapper;

import com.prahlad.aijobportal.authservice.auth.dto.response.UserResponse;
import com.prahlad.aijobportal.authservice.user.entity.Role;
import com.prahlad.aijobportal.authservice.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toUserResponse(User user);

    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
