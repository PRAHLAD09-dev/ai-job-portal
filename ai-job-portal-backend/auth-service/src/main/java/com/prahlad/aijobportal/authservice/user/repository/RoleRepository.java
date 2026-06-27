package com.prahlad.aijobportal.authservice.user.repository;

import com.prahlad.aijobportal.authservice.user.entity.Role;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);
}
