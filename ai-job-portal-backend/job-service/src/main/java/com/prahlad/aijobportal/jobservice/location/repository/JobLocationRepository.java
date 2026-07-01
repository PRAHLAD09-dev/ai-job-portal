package com.prahlad.aijobportal.jobservice.location.repository;

import com.prahlad.aijobportal.jobservice.location.entity.JobLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobLocationRepository extends JpaRepository<JobLocation, UUID> {

    List<JobLocation> findByJobId(UUID jobId);
}
