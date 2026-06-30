package com.prahlad.aijobportal.recruiterservice.location.service.impl;

import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyAccessGuard;
import com.prahlad.aijobportal.recruiterservice.company.exception.CompanyLocationNotFoundException;
import com.prahlad.aijobportal.recruiterservice.location.dto.request.CompanyLocationRequest;
import com.prahlad.aijobportal.recruiterservice.location.dto.response.CompanyLocationResponse;
import com.prahlad.aijobportal.recruiterservice.location.entity.CompanyLocation;
import com.prahlad.aijobportal.recruiterservice.location.mapper.CompanyLocationMapper;
import com.prahlad.aijobportal.recruiterservice.location.repository.CompanyLocationRepository;
import com.prahlad.aijobportal.recruiterservice.location.service.CompanyLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyLocationServiceImpl implements CompanyLocationService {

    private final CompanyLocationRepository companyLocationRepository;
    private final CompanyLocationMapper companyLocationMapper;
    private final CompanyAccessGuard companyAccessGuard;

    @Override
    @Transactional
    public CompanyLocationResponse create(UUID userId, CompanyLocationRequest request) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        if (request.headquarters()) {
            demoteExistingHeadquarters(company.getId());
        }

        CompanyLocation location = companyLocationMapper.toEntity(request);
        location.setCompany(company);

        CompanyLocation saved = companyLocationRepository.save(location);
        log.info("Created location id={} for companyId={}", saved.getId(), company.getId());
        return companyLocationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyLocationResponse> getAll(UUID userId) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);
        return companyLocationRepository.findByCompanyId(company.getId()).stream()
                .map(companyLocationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CompanyLocationResponse update(UUID userId, UUID locationId, CompanyLocationRequest request) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        CompanyLocation location = companyLocationRepository.findByIdAndCompanyId(locationId, company.getId())
                .orElseThrow(() -> new CompanyLocationNotFoundException(locationId));

        if (request.headquarters() && !location.isHeadquarters()) {
            demoteExistingHeadquarters(company.getId());
        }

        companyLocationMapper.updateEntityFromRequest(request, location);
        CompanyLocation saved = companyLocationRepository.save(location);

        log.info("Updated location id={} for companyId={}", saved.getId(), company.getId());
        return companyLocationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID locationId) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        CompanyLocation location = companyLocationRepository.findByIdAndCompanyId(locationId, company.getId())
                .orElseThrow(() -> new CompanyLocationNotFoundException(locationId));

        companyLocationRepository.delete(location);
        log.info("Deleted location id={} for companyId={}", locationId, company.getId());
    }

    private void demoteExistingHeadquarters(UUID companyId) {
        companyLocationRepository.findByCompanyId(companyId).stream()
                .filter(CompanyLocation::isHeadquarters)
                .forEach(existing -> {
                    existing.setHeadquarters(false);
                    companyLocationRepository.save(existing);
                });
    }
}
