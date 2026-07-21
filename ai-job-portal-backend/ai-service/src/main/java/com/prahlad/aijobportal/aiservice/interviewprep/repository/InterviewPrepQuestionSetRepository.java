package com.prahlad.aijobportal.aiservice.interviewprep.repository;

import com.prahlad.aijobportal.aiservice.interviewprep.entity.InterviewPrepQuestionSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InterviewPrepQuestionSetRepository extends JpaRepository<InterviewPrepQuestionSet, UUID> {

    Optional<InterviewPrepQuestionSet> findTopByCandidateIdOrderByCreatedAtDesc(UUID candidateId);

    /** Scopes a lookup-by-id to the owning candidate, enforcing "candidate can only access their own question sets" structurally. */
    Optional<InterviewPrepQuestionSet> findByIdAndCandidateId(UUID id, UUID candidateId);
}
