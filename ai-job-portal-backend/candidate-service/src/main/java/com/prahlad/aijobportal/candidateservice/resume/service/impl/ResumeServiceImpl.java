package com.prahlad.aijobportal.candidateservice.resume.service.impl;

import com.prahlad.aijobportal.candidateservice.candidate.entity.Candidate;
import com.prahlad.aijobportal.candidateservice.candidate.service.CandidateLookupService;
import com.prahlad.aijobportal.candidateservice.candidate.service.ProfileCompletionService;
import com.prahlad.aijobportal.candidateservice.event.CandidateEventPublisher;
import com.prahlad.aijobportal.candidateservice.event.dto.ResumeUploadedEvent;
import com.prahlad.aijobportal.candidateservice.resume.config.ResumeProperties;
import com.prahlad.aijobportal.candidateservice.resume.dto.response.ResumeResponse;
import com.prahlad.aijobportal.candidateservice.resume.entity.Resume;
import com.prahlad.aijobportal.candidateservice.resume.enums.ResumeStatus;
import com.prahlad.aijobportal.candidateservice.resume.exception.ResumeNotFoundException;
import com.prahlad.aijobportal.candidateservice.resume.mapper.ResumeMapper;
import com.prahlad.aijobportal.candidateservice.resume.repository.ResumeRepository;
import com.prahlad.aijobportal.candidateservice.resume.service.CloudinaryUploadResult;
import com.prahlad.aijobportal.candidateservice.resume.service.FileStorageService;
import com.prahlad.aijobportal.candidateservice.resume.service.ResumeService;
import com.prahlad.aijobportal.candidateservice.resume.util.ResumeFileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CandidateEventPublisher candidateEventPublisher;
    private final ResumeProperties resumeProperties;

    @Override
    @Transactional
    public ResumeResponse upload(UUID userId, MultipartFile file) {
        resumeFileValidator.validate(file);
        Candidate candidate = candidateLookupService.getByUserIdOrThrow(userId);

        CloudinaryUploadResult uploadResult = fileStorageService.upload(file, resumeProperties.getCloudinaryFolder());

        // Demote any currently-ACTIVE resume to ARCHIVED: exactly one
        // resume stays ACTIVE at a time, older versions are kept for
        // version history rather than deleted.
        resumeRepository.findByCandidateIdAndStatus(candidate.getId(), ResumeStatus.ACTIVE)
                .ifPresent(previous -> {
                    previous.setStatus(ResumeStatus.ARCHIVED);
                    resumeRepository.save(previous);
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

        Resume saved = resumeRepository.save(resume);
        profileCompletionService.recalculate(candidate);

        candidateEventPublisher.publishResumeUploaded(new ResumeUploadedEvent(
                candidate.getId(), userId, saved.getId(), saved.getFileUrl(), saved.getVersionNumber(), Instant.now()
        ));

        log.info("Uploaded resume id={} version={} for candidateId={}", saved.getId(), nextVersion, candidate.getId());
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

        existing.setFileName(file.getOriginalFilename());
        existing.setFileUrl(uploadResult.secureUrl());
        existing.setCloudinaryPublicId(uploadResult.publicId());
        existing.setFileFormat(uploadResult.format());
        existing.setFileSizeBytes(uploadResult.bytes());

        Resume saved = resumeRepository.save(existing);

        fileStorageService.delete(oldPublicId);

        candidateEventPublisher.publishResumeUploaded(new ResumeUploadedEvent(
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

        resumeRepository.delete(resume);
        fileStorageService.delete(resume.getCloudinaryPublicId());
        profileCompletionService.recalculate(candidate);

        log.info("Deleted resume id={} for candidateId={}", resumeId, candidate.getId());
    }
}
