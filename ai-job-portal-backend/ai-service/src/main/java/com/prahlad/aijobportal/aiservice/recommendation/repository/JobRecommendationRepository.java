package com.prahlad.aijobportal.aiservice.recommendation.repository;

import com.prahlad.aijobportal.aiservice.recommendation.entity.JobRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRecommendationRepository extends JpaRepository<JobRecommendation, UUID> {

    List<JobRecommendation> findByCandidateIdOrderByMatchScoreDesc(UUID candidateId);

    Optional<JobRecommendation> findByCandidateIdAndJobId(UUID candidateId, UUID jobId);

    void deleteByCandidateId(UUID candidateId);
}
