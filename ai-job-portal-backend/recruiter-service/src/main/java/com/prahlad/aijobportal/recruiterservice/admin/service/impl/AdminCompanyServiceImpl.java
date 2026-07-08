package com.prahlad.aijobportal.recruiterservice.admin.service.impl;

import com.prahlad.aijobportal.recruiterservice.admin.dto.response.AdminCompanyResponse;
import com.prahlad.aijobportal.recruiterservice.admin.dto.response.CompanyPlatformStatisticsResponse;
import com.prahlad.aijobportal.recruiterservice.admin.mapper.AdminCompanyMapper;
import com.prahlad.aijobportal.recruiterservice.admin.service.AdminCompanyService;
import com.prahlad.aijobportal.recruiterservice.admin.specification.AdminCompanySpecification;
import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import com.prahlad.aijobportal.recruiterservice.company.repository.CompanyRepository;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminCompanyServiceImpl implements AdminCompanyService {

    private final CompanyRepository companyRepository;
    private final AdminCompanyMapper adminCompanyMapper;

    @Override
    public Page<AdminCompanyResponse> searchCompanies(String keyword, VerificationStatus status, Pageable pageable) {
        return companyRepository.findAll(AdminCompanySpecification.withCriteria(keyword, status), pageable)
                .map(adminCompanyMapper::toResponse);
    }

    @Override
    public AdminCompanyResponse getCompany(UUID companyId) {
        return adminCompanyMapper.toResponse(findCompanyOrThrow(companyId));
    }

    @Override
    @Transactional
    public AdminCompanyResponse verifyCompany(UUID companyId) {
        Company company = findCompanyOrThrow(companyId);
        company.setVerificationStatus(VerificationStatus.VERIFIED);
        log.info("Admin action: company {} verified", companyId);
        return adminCompanyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public AdminCompanyResponse rejectCompany(UUID companyId) {
        Company company = findCompanyOrThrow(companyId);
        company.setVerificationStatus(VerificationStatus.REJECTED);
        log.info("Admin action: company {} rejected", companyId);
        return adminCompanyMapper.toResponse(company);
    }

    @Override
    @Transactional
    public AdminCompanyResponse suspendCompany(UUID companyId) {
        Company company = findCompanyOrThrow(companyId);
        company.setVerificationStatus(VerificationStatus.SUSPENDED);
        log.info("Admin action: company {} suspended", companyId);
        return adminCompanyMapper.toResponse(company);
    }

    @Override
    public CompanyPlatformStatisticsResponse getPlatformStatistics() {
        return new CompanyPlatformStatisticsResponse(
                companyRepository.count(),
                companyRepository.countByVerificationStatus(VerificationStatus.PENDING),
                companyRepository.countByVerificationStatus(VerificationStatus.VERIFIED),
                companyRepository.countByVerificationStatus(VerificationStatus.REJECTED),
                companyRepository.countByVerificationStatus(VerificationStatus.SUSPENDED)
        );
    }

    private Company findCompanyOrThrow(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
    }
}
