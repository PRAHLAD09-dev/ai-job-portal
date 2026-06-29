package com.prahlad.aijobportal.candidateservice.resume.repository;

import com.prahlad.aijobportal.candidateservice.resume.entity.Resume;
import com.prahlad.aijobportal.candidateservice.resume.enums.ResumeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    List<Resume> findByCandidateIdOrderByVersionNumberDesc(UUID candidateId);

    Optional<Resume> findByIdAndCandidateId(UUID id, UUID candidateId);

    Optional<Resume> findByCandidateIdAndStatus(UUID candidateId, ResumeStatus status);

    @Query("select coalesce(max(r.versionNumber), 0) from Resume r where r.candidate.id = :candidateId")
    int findMaxVersionNumberByCandidateId(@Param("candidateId") UUID candidateId);
}
