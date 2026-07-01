package com.prahlad.aijobportal.jobservice.job.repository;

import com.prahlad.aijobportal.jobservice.job.entity.JobAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobAlertRepository extends JpaRepository<JobAlert, UUID> {

    List<JobAlert> findByUserId(UUID userId);

    Optional<JobAlert> findByIdAndUserId(UUID id, UUID userId);
}
