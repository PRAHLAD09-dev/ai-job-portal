package com.prahlad.aijobportal.applicationservice.timeline.entity;

import com.prahlad.aijobportal.applicationservice.application.entity.JobApplication;
import com.prahlad.aijobportal.applicationservice.application.enums.ApplicationStatus;
import com.prahlad.aijobportal.applicationservice.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * An immutable audit record of a single status transition on a
 * {@link com.prahlad.aijobportal.applicationservice.application.entity.JobApplication}.
 * Per DAY06_APPLICATION_SERVICE.md's "Timeline" section, every status
 * change creates one of these automatically — application-service
 * never mutates an existing timeline row.
 */
@Entity
@Table(name = "application_timeline", indexes = {
        @Index(name = "idx_timeline_application", columnList = "application_id")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true)
public class ApplicationTimeline extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private ApplicationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private ApplicationStatus newStatus;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
