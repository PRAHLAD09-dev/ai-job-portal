package com.prahlad.aijobportal.recruiterservice.admin.service.impl;

import com.prahlad.aijobportal.recruiterservice.admin.dto.response.AdminCompanyResponse;
import com.prahlad.aijobportal.recruiterservice.admin.mapper.AdminCompanyMapper;
import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import com.prahlad.aijobportal.recruiterservice.company.repository.CompanyRepository;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCompanyServiceImplTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private AdminCompanyMapper adminCompanyMapper;

    private AdminCompanyServiceImpl adminCompanyService;

    private UUID companyId;
    private Company company;

    @BeforeEach
    void setUp() {
        adminCompanyService = new AdminCompanyServiceImpl(companyRepository, adminCompanyMapper);
        companyId = UUID.randomUUID();
        company = Company.builder()
                .name("Acme Corp")
                .slug("acme-corp")
                .verificationStatus(VerificationStatus.PENDING)
                .build();
    }

    @Test
    void suspendCompany_setsStatusToSuspended() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(adminCompanyMapper.toResponse(any(Company.class))).thenAnswer(invocation -> {
            Company c = invocation.getArgument(0);
            return new AdminCompanyResponse(companyId, c.getName(), c.getSlug(), null, null,
                    null, null, null, c.getVerificationStatus(), 0, 0, Instant.now(), Instant.now());
        });

        AdminCompanyResponse response = adminCompanyService.suspendCompany(companyId);

        assertThat(response.verificationStatus()).isEqualTo(VerificationStatus.SUSPENDED);
        assertThat(company.getVerificationStatus()).isEqualTo(VerificationStatus.SUSPENDED);
    }

    @Test
    void getCompany_whenCompanyDoesNotExist_throwsResourceNotFoundException() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCompanyService.getCompany(companyId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
