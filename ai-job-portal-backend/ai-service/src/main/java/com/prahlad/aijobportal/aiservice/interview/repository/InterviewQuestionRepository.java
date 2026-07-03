package com.prahlad.aijobportal.aiservice.interview.repository;

import com.prahlad.aijobportal.aiservice.interview.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, UUID> {

    List<InterviewQuestion> findByJobId(UUID jobId);
}
