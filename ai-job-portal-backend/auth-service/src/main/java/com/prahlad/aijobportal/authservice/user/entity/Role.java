package com.prahlad.aijobportal.authservice.user.entity;

import com.prahlad.aijobportal.authservice.config.BaseEntity;
import com.prahlad.aijobportal.authservice.user.enums.RoleName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * A platform role (CANDIDATE, RECRUITER, ADMIN) that can be assigned to a
 * {@link User}. Roles are reference data: they are seeded once via Flyway
 * and are not created/deleted through the application APIs in this phase.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 30)
    private RoleName name;
}
