package com.prahlad.aijobportal.recruiterservice.config;

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
 * for every entity owned by the Recruiter Service. Kept local to this
 * service (mirroring the same approach used in Auth Service and
 * Candidate Service) rather than the shared common module, because the
 * common module is explicitly framework-agnostic and must never contain
 * JPA entities.
 *
 * Uses Lombok's {@code @SuperBuilder} so that subclass builders can also
 * set the {@code id} / {@code createdAt} / {@code updatedAt} fields
 * declared here.
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
