package com.prahlad.aijobportal.jobservice.benefit.repository;

import com.prahlad.aijobportal.jobservice.benefit.entity.JobBenefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobBenefitRepository extends JpaRepository<JobBenefit, UUID> {

    List<JobBenefit> findByJobId(UUID jobId);
}
