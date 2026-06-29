package com.prahlad.aijobportal.candidateservice.resume.entity;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.config.BaseEntity;
import com.prahlad.aijobportal.candidateservice.resume.enums.ResumeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Metadata for a single resume file uploaded by a {@link Candidate}.
 * The actual file bytes live in Cloudinary — per DECISIONS.md (File
 * Storage: Cloudinary, store only URLs, no BLOB storage), this entity
 * persists ONLY the Cloudinary URL and identifiers needed to manage
 * (replace/delete) the asset, never the file content itself.
 *
 * A candidate may keep multiple resume versions; exactly one is
 * {@code ACTIVE} at a time (the default used when applying to jobs),
 * with older versions retained as {@code ARCHIVED} for version history
 * per DAY03's "Resume Versioning" requirement.
 */
@Entity
@Table(name = "resumes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"candidate"})
public class Resume extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "cloudinary_public_id", nullable = false, length = 500)
    private String cloudinaryPublicId;

    @Column(name = "file_format", nullable = false, length = 20)
    private String fileFormat;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ResumeStatus status = ResumeStatus.ACTIVE;
}
