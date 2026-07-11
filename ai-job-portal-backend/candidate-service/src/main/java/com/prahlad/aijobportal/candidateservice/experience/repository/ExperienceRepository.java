package com.prahlad.aijobportal.candidateservice.experience.repository;

import com.prahlad.aijobportal.candidateservice.experience.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

    List<Experience> findByCandidateId(UUID candidateId);

    Optional<Experience> findByIdAndCandidateId(UUID id, UUID candidateId);

    boolean existsByCandidateId(UUID candidateId);
}
