package com.prahlad.aijobportal.jobservice.requirement.repository;

import com.prahlad.aijobportal.jobservice.requirement.entity.JobRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRequirementRepository extends JpaRepository<JobRequirement, UUID> {

    List<JobRequirement> findByJobIdOrderByDisplayOrderAsc(UUID jobId);
}
