package com.prahlad.aijobportal.recruiterservice.company.service.impl;

import com.prahlad.aijobportal.recruiterservice.company.dto.request.CreateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.request.UpdateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyPublicResponse;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyResponse;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.mapper.CompanyMapper;
import com.prahlad.aijobportal.recruiterservice.company.repository.CompanyRepository;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyAccessGuard;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyService;
import com.prahlad.aijobportal.recruiterservice.company.util.SlugGenerator;
import com.prahlad.aijobportal.recruiterservice.event.RecruiterEventPublisher;
import com.prahlad.aijobportal.recruiterservice.event.dto.CompanyCreatedEvent;
import com.prahlad.aijobportal.recruiterservice.event.dto.CompanyUpdatedEvent;
import com.prahlad.aijobportal.recruiterservice.feign.dto.UserSummaryResponse;
import com.prahlad.aijobportal.recruiterservice.recruiter.entity.Recruiter;
import com.prahlad.aijobportal.recruiterservice.recruiter.exception.RecruiterProfileAlreadyExistsException;
import com.prahlad.aijobportal.recruiterservice.recruiter.repository.RecruiterRepository;
import com.prahlad.aijobportal.recruiterservice.recruiter.service.AuthUserLookupService;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyMapper companyMapper;
    private final CompanyAccessGuard companyAccessGuard;
    private final SlugGenerator slugGenerator;
    private final AuthUserLookupService authUserLookupService;
    private final RecruiterEventPublisher recruiterEventPublisher;

    @Override
    @Transactional
    public CompanyResponse createCompany(UUID userId, String bearerToken, CreateCompanyRequest request) {
        if (recruiterRepository.existsByUserId(userId)) {
            throw new RecruiterProfileAlreadyExistsException(
                    "A recruiter profile already exists for this account. Each account may own only one company.");
        }

        UserSummaryResponse authUser = authUserLookupService.fetchCurrentUser(bearerToken);
        String slug = slugGenerator.generateUniqueSlug(request.name());

        Company company = Company.builder()
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .industry(request.industry())
                .companySize(request.companySize())
                .foundedYear(request.foundedYear())
                .websiteUrl(request.websiteUrl())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();

        Company savedCompany = companyRepository.save(company);

        Recruiter recruiter = Recruiter.builder()
                .userId(userId)
                .email(authUser.email())
                .fullName(authUser.firstName() + " " + authUser.lastName())
                .phoneNumber(request.recruiterPhoneNumber())
                .title(request.recruiterTitle())
                .designation(request.recruiterDesignation())
                .owner(true)
                .company(savedCompany)
                .build();
        recruiterRepository.save(recruiter);

        recruiterEventPublisher.publishCompanyCreated(new CompanyCreatedEvent(
                savedCompany.getId(), userId, savedCompany.getName(), savedCompany.getSlug(), Instant.now()
        ));

        log.info("Created company id={} with owner userId={}", savedCompany.getId(), userId);
        return companyMapper.toResponse(savedCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getMyCompany(UUID userId) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);
        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public CompanyResponse updateMyCompany(UUID userId, UpdateCompanyRequest request) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        companyMapper.updateEntityFromRequest(request, company);
        Company saved = companyRepository.save(company);

        recruiterEventPublisher.publishCompanyUpdated(new CompanyUpdatedEvent(
                saved.getId(), saved.getName(), saved.getSlug(), Instant.now()
        ));

        log.info("Updated company id={}", saved.getId());
        return companyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMyCompany(UUID userId) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);
        companyRepository.delete(company);
        log.info("Deleted company id={}", company.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyPublicResponse getPublicProfile(String slug) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "slug", slug));
        return companyMapper.toPublicResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyStatisticsResponse getMyCompanyStatistics(UUID userId) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);
        return new CompanyStatisticsResponse(
                company.getId(),
                company.getActiveJobCount(),
                company.getTotalHires(),
                company.getRecruiters().size(),
                company.getVerificationStatus()
        );
    }
}
