package com.prahlad.aijobportal.recruiterservice.location.repository;

import com.prahlad.aijobportal.recruiterservice.location.entity.CompanyLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyLocationRepository extends JpaRepository<CompanyLocation, UUID> {

    List<CompanyLocation> findByCompanyId(UUID companyId);

    Optional<CompanyLocation> findByIdAndCompanyId(UUID id, UUID companyId);
}
