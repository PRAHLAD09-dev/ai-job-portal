package com.prahlad.aijobportal.aiservice.resumeanalysis.repository;

import com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, UUID> {

    Optional<ResumeAnalysis> findTopByCandidateIdAndResumeTextHashOrderByCreatedAtDesc(UUID candidateId, String resumeTextHash);

    Optional<ResumeAnalysis> findTopByCandidateIdOrderByCreatedAtDesc(UUID candidateId);
}
