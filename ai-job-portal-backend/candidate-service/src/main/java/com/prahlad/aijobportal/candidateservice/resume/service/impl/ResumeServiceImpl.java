package com.prahlad.aijobportal.candidateservice.resume.service.impl;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateLookupService;
import com.prahlad.aijobportal.candidateservice.candidate.service.ProfileCompletionService;
import com.prahlad.aijobportal.candidateservice.event.dto.ResumeUploadedEvent;
import com.prahlad.aijobportal.candidateservice.resume.config.ResumeProperties;
import com.prahlad.aijobportal.candidateservice.resume.dto.response.ResumeResponse;
import com.prahlad.aijobportal.candidateservice.resume.entity.Resume;
import com.prahlad.aijobportal.candidateservice.resume.enums.ResumeStatus;
import com.prahlad.aijobportal.candidateservice.resume.event.ResumeFileCleanupEvent;
import com.prahlad.aijobportal.candidateservice.resume.exception.ResumeNotFoundException;
import com.prahlad.aijobportal.candidateservice.resume.exception.ResumeUploadConflictException;
import com.prahlad.aijobportal.candidateservice.resume.mapper.ResumeMapper;
import com.prahlad.aijobportal.candidateservice.resume.repository.ResumeRepository;
import com.prahlad.aijobportal.candidateservice.resume.service.CloudinaryUploadResult;
import com.prahlad.aijobportal.candidateservice.resume.service.FileStorageService;
import com.prahlad.aijobportal.candidateservice.resume.service.ResumeService;
import com.prahlad.aijobportal.candidateservice.resume.util.ResumeFileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final CandidateLookupService candidateLookupService;
    private final ProfileCompletionService profileCompletionService;
    private final FileStorageService fileStorageService;
    private final ResumeFileValidator resumeFileValidator;
    private final ResumeProperties resumeProperties;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public ResumeResponse upload(UUID userId, MultipartFile file) {
        resumeFileValidator.validate(file);
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        // Cloudinary upload has to happen before the DB write, since the
        // Resume row needs the resulting URL/publicId to be persisted at
        // all. That ordering is unavoidable, but it means a DB failure
        // AFTER a successful upload would otherwise leave an orphaned,
        // permanently-billed Cloudinary asset with no application record
        // pointing to it. The try/catch below compensates for that: any
        // failure during the DB portion deletes the just-uploaded file.
        CloudinaryUploadResult uploadResult = fileStorageService.upload(file, resumeProperties.getCloudinaryFolder());

        Resume saved;
        try {
            // Demote any currently-ACTIVE resume to ARCHIVED: exactly one
            // resume stays ACTIVE at a time, older versions are kept for
            // version history rather than deleted. saveAndFlush forces
            // this UPDATE to execute before the INSERT below, so the
            // partial unique index (uk_resumes_one_active_per_candidate)
            // never sees two ACTIVE rows for this candidate even
            // momentarily within the same flush.
            resumeRepository.findByCandidateIdAndStatus(candidate.getId(), ResumeStatus.ACTIVE)
                    .ifPresent(previous -> {
                        previous.setStatus(ResumeStatus.ARCHIVED);
                        resumeRepository.saveAndFlush(previous);
                    });

            int nextVersion = resumeRepository.findMaxVersionNumberByCandidateId(candidate.getId()) + 1;

            Resume resume = Resume.builder()
                    .candidate(candidate)
                    .fileName(file.getOriginalFilename())
                    .fileUrl(uploadResult.secureUrl())
                    .cloudinaryPublicId(uploadResult.publicId())
                    .fileFormat(uploadResult.format())
                    .fileSizeBytes(uploadResult.bytes())
                    .versionNumber(nextVersion)
                    .status(ResumeStatus.ACTIVE)
                    .build();

            // saveAndFlush (not save) so that a concurrent upload racing
            // for the same version number or ACTIVE slot surfaces its
            // constraint violation synchronously, right here, rather than
            // at some later unrelated flush point.
            saved = resumeRepository.saveAndFlush(resume);
        } catch (DataIntegrityViolationException ex) {
            // Another upload for this candidate committed first (took the
            // ACTIVE slot and/or this version number) between our reads
            // above and this flush. Compensate for the orphaned upload and
            // surface a clean, actionable conflict instead of a raw
            // constraint-violation 500.
            safeDeleteCompensation(uploadResult.publicId());
            throw new ResumeUploadConflictException(
                    "A concurrent resume upload was detected for this profile. Please try uploading again.");
        } catch (RuntimeException ex) {
            safeDeleteCompensation(uploadResult.publicId());
            throw ex;
        }

        profileCompletionService.recalculate(candidate);

        applicationEventPublisher.publishEvent(new ResumeUploadedEvent(
                candidate.getId(), userId, saved.getId(), saved.getFileUrl(), saved.getVersionNumber(), Instant.now()
        ));

        log.info("Uploaded resume id={} version={} for candidateId={}", saved.getId(), saved.getVersionNumber(),
                candidate.getId());
        return resumeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeResponse> getAll(UUID userId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);
        return resumeRepository.findByCandidateIdOrderByVersionNumberDesc(candidate.getId()).stream()
                .map(resumeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ResumeResponse replace(UUID userId, UUID resumeId, MultipartFile file) {
        resumeFileValidator.validate(file);
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Resume existing = resumeRepository.findByIdAndCandidateId(resumeId, candidate.getId())
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        String oldPublicId = existing.getCloudinaryPublicId();

        CloudinaryUploadResult uploadResult = fileStorageService.upload(file, resumeProperties.getCloudinaryFolder());

        Resume saved;
        try {
            existing.setFileName(file.getOriginalFilename());
            existing.setFileUrl(uploadResult.secureUrl());
            existing.setCloudinaryPublicId(uploadResult.publicId());
            existing.setFileFormat(uploadResult.format());
            existing.setFileSizeBytes(uploadResult.bytes());

            saved = resumeRepository.saveAndFlush(existing);
        } catch (RuntimeException ex) {
            // The DB update failed/rolled back - the newly-uploaded file
            // is now unreferenced by any row. Compensate by deleting it;
            // the OLD file is untouched and remains correctly referenced.
            safeDeleteCompensation(uploadResult.publicId());
            throw ex;
        }

        // Deleting the OLD file is deferred to AFTER_COMMIT rather than
        // done synchronously here: if this Cloudinary delete call were to
        // throw, it would otherwise roll back the DB update we just made
        // (undoing a successful replace) while the NEW file we just
        // uploaded would be left orphaned anyway. Doing it after commit
        // means a delete failure only leaves the old file as a harmless,
        // logged-for-cleanup orphan instead of corrupting an otherwise
        // successful replace.
        applicationEventPublisher.publishEvent(new ResumeFileCleanupEvent(oldPublicId));

        applicationEventPublisher.publishEvent(new ResumeUploadedEvent(
                candidate.getId(), userId, saved.getId(), saved.getFileUrl(), saved.getVersionNumber(), Instant.now()
        ));

        log.info("Replaced resume id={} for candidateId={}", saved.getId(), candidate.getId());
        return resumeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID resumeId) {
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        Resume resume = resumeRepository.findByIdAndCandidateId(resumeId, candidate.getId())
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        String publicId = resume.getCloudinaryPublicId();

        resumeRepository.delete(resume);
        profileCompletionService.recalculate(candidate);

        // Same AFTER_COMMIT rationale as replace(): the DB is the source
        // of truth for "this resume no longer exists." Deleting the
        // Cloudinary asset is a best-effort cleanup that must not be able
        // to roll back an otherwise-successful DB delete.
        applicationEventPublisher.publishEvent(new ResumeFileCleanupEvent(publicId));

        log.info("Deleted resume id={} for candidateId={}", resumeId, candidate.getId());
    }

    private void safeDeleteCompensation(String publicId) {
        try {
            fileStorageService.delete(publicId);
        } catch (Exception cleanupEx) {
            log.error("Failed to compensate/cleanup orphaned Cloudinary asset publicId={} after a failed "
                    + "database write; manual cleanup may be required", publicId, cleanupEx);
        }
    }
}
