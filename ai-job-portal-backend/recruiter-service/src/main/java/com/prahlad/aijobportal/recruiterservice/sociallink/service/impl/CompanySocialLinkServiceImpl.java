package com.prahlad.aijobportal.recruiterservice.sociallink.service.impl;

import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.exception.SocialLinkAlreadyExistsException;
import com.prahlad.aijobportal.recruiterservice.company.exception.SocialLinkNotFoundException;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyAccessGuard;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.request.CompanySocialLinkRequest;
import com.prahlad.aijobportal.recruiterservice.sociallink.dto.response.CompanySocialLinkResponse;
import com.prahlad.aijobportal.recruiterservice.sociallink.entity.CompanySocialLink;
import com.prahlad.aijobportal.recruiterservice.sociallink.mapper.CompanySocialLinkMapper;
import com.prahlad.aijobportal.recruiterservice.sociallink.repository.CompanySocialLinkRepository;
import com.prahlad.aijobportal.recruiterservice.sociallink.service.CompanySocialLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanySocialLinkServiceImpl implements CompanySocialLinkService {

    private final CompanySocialLinkRepository companySocialLinkRepository;
    private final CompanySocialLinkMapper companySocialLinkMapper;
    private final CompanyAccessGuard companyAccessGuard;

    @Override
    @Transactional
    public CompanySocialLinkResponse create(UUID userId, CompanySocialLinkRequest request) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        if (companySocialLinkRepository.existsByCompanyIdAndPlatform(company.getId(), request.platform())) {
            throw new SocialLinkAlreadyExistsException(request.platform().name());
        }

        CompanySocialLink link = companySocialLinkMapper.toEntity(request);
        link.setCompany(company);

        CompanySocialLink saved = companySocialLinkRepository.save(link);
        log.info("Created social link id={} for companyId={}", saved.getId(), company.getId());
        return companySocialLinkMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanySocialLinkResponse> getAll(UUID userId) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);
        return companySocialLinkRepository.findByCompanyId(company.getId()).stream()
                .map(companySocialLinkMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CompanySocialLinkResponse update(UUID userId, UUID linkId, CompanySocialLinkRequest request) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        CompanySocialLink link = companySocialLinkRepository.findByIdAndCompanyId(linkId, company.getId())
                .orElseThrow(() -> new SocialLinkNotFoundException(linkId));

        if (link.getPlatform() != request.platform()
                && companySocialLinkRepository.existsByCompanyIdAndPlatform(company.getId(), request.platform())) {
            throw new SocialLinkAlreadyExistsException(request.platform().name());
        }

        companySocialLinkMapper.updateEntityFromRequest(request, link);
        CompanySocialLink saved = companySocialLinkRepository.save(link);

        log.info("Updated social link id={} for companyId={}", saved.getId(), company.getId());
        return companySocialLinkMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID linkId) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        CompanySocialLink link = companySocialLinkRepository.findByIdAndCompanyId(linkId, company.getId())
                .orElseThrow(() -> new SocialLinkNotFoundException(linkId));

        companySocialLinkRepository.delete(link);
        log.info("Deleted social link id={} for companyId={}", linkId, company.getId());
    }
}
