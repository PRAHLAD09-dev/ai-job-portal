package com.prahlad.aijobportal.recruiterservice.company.repository;

import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {

    Optional<Company> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // ---- Added for Admin Service (DAY09_ADMIN_SERVICE.md): platform
    // statistics + dynamic search/filter (JpaSpecificationExecutor above). ----
    long countByVerificationStatus(VerificationStatus status);
}
