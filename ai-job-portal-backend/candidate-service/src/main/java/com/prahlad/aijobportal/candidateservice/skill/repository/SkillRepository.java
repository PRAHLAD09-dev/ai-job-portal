package com.prahlad.aijobportal.candidateservice.skill.repository;

import com.prahlad.aijobportal.candidateservice.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    List<Skill> findByCandidateId(UUID candidateId);

    Optional<Skill> findByIdAndCandidateId(UUID id, UUID candidateId);

    boolean existsByCandidateIdAndNameIgnoreCase(UUID candidateId, String name);
}
