package com.prahlad.aijobportal.recruiterservice.sociallink.repository;

import com.prahlad.aijobportal.recruiterservice.sociallink.entity.CompanySocialLink;
import com.prahlad.aijobportal.recruiterservice.sociallink.enums.SocialPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanySocialLinkRepository extends JpaRepository<CompanySocialLink, UUID> {

    List<CompanySocialLink> findByCompanyId(UUID companyId);

    Optional<CompanySocialLink> findByIdAndCompanyId(UUID id, UUID companyId);

    boolean existsByCompanyIdAndPlatform(UUID companyId, SocialPlatform platform);
}
