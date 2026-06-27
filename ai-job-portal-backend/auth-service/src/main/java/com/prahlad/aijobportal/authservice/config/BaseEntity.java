package com.prahlad.aijobportal.authservice.config;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Common JPA base class providing a UUID primary key and audit timestamps
 * for every entity owned by the Auth Service. Kept local to this service
 * (rather than the shared common module) because the common module is
 * explicitly framework-agnostic and must never contain JPA entities.
 *
 * Uses Lombok's {@code @SuperBuilder} (rather than plain {@code @Builder})
 * so that subclass builders can also set the {@code id} / {@code createdAt}
 * / {@code updatedAt} fields declared here — needed for test fixtures and
 * for reconstructing entities with a known id.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
