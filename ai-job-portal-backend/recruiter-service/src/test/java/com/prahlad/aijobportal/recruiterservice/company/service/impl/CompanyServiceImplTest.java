package com.prahlad.aijobportal.recruiterservice.company.service.impl;

import com.prahlad.aijobportal.recruiterservice.company.dto.request.CreateCompanyRequest;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyResponse;
import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.enums.CompanySize;
import com.prahlad.aijobportal.recruiterservice.company.enums.Industry;
import com.prahlad.aijobportal.recruiterservice.company.enums.VerificationStatus;
import com.prahlad.aijobportal.recruiterservice.company.mapper.CompanyMapper;
import com.prahlad.aijobportal.recruiterservice.company.repository.CompanyRepository;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyAccessGuard;
import com.prahlad.aijobportal.recruiterservice.company.util.SlugGenerator;
import com.prahlad.aijobportal.recruiterservice.event.dto.CompanyCreatedEvent;
import com.prahlad.aijobportal.recruiterservice.feign.dto.UserSummaryResponse;
import com.prahlad.aijobportal.recruiterservice.recruiter.entity.Recruiter;
import com.prahlad.aijobportal.recruiterservice.recruiter.enums.RecruiterTitle;
import com.prahlad.aijobportal.recruiterservice.recruiter.exception.RecruiterProfileAlreadyExistsException;
import com.prahlad.aijobportal.recruiterservice.recruiter.repository.RecruiterRepository;
import com.prahlad.aijobportal.recruiterservice.recruiter.service.AuthUserLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private RecruiterRepository recruiterRepository;
    @Mock private CompanyMapper companyMapper;
    @Mock private CompanyAccessGuard companyAccessGuard;
    @Mock private SlugGenerator slugGenerator;
    @Mock private AuthUserLookupService authUserLookupService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private UserSummaryResponse authUser;
    private CreateCompanyRequest request;

    @BeforeEach
    void setUp() {
        authUser = new UserSummaryResponse(
                UUID.randomUUID(), "john.doe@example.com", "John", "Doe",
                Set.of("RECRUITER"), "ACTIVE", true, Instant.now());

        request = new CreateCompanyRequest(
                "Acme Corp", "A great company", Industry.INFORMATION_TECHNOLOGY,
                CompanySize.SIZE_11_50, 2010, "https://acme.com",
                "contact@acme.com", "+1-555-0100",
                RecruiterTitle.HR_MANAGER, "HR Manager", "+1-555-0101");
    }

    @Test
    void createCompany_success_createsCompanyAndRecruiter() {
        UUID userId = UUID.randomUUID();
        String slug = "acme-corp";

        when(recruiterRepository.existsByUserId(userId)).thenReturn(false);
        when(authUserLookupService.fetchCurrentUser(any())).thenReturn(authUser);
        when(slugGenerator.generateUniqueSlug("Acme Corp")).thenReturn(slug);

        Company savedCompany = Company.builder()
                .name("Acme Corp").slug(slug).industry(Industry.INFORMATION_TECHNOLOGY)
                .companySize(CompanySize.SIZE_11_50).verificationStatus(VerificationStatus.PENDING)
                .build();
        savedCompany.setId(UUID.randomUUID());

        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        CompanyResponse expectedResponse = new CompanyResponse(
                savedCompany.getId(), "Acme Corp", slug, "A great company",
                Industry.INFORMATION_TECHNOLOGY, CompanySize.SIZE_11_50, 2010,
                "https://acme.com", "contact@acme.com", "+1-555-0100",
                null, null, VerificationStatus.PENDING, 0, 0, List.of(), List.of());
        when(companyMapper.toResponse(savedCompany)).thenReturn(expectedResponse);

        CompanyResponse response = companyService.createCompany(userId, "Bearer token", request);

        assertThat(response.slug()).isEqualTo(slug);
        assertThat(response.name()).isEqualTo("Acme Corp");
        // saveAndFlush (not save) since M2's fix forces the versioned/unique-
        // constraint conflict to surface synchronously within createCompany().
        verify(recruiterRepository).saveAndFlush(any(Recruiter.class));
        // Publishing moved to a Spring ApplicationEvent, consumed AFTER_COMMIT
        // by CompanyEventListener (M1's fix) - the unit under test only
        // needs to prove the event was raised, not that Kafka was called.
        verify(applicationEventPublisher).publishEvent(any(CompanyCreatedEvent.class));
    }

    @Test
    void createCompany_whenRecruiterAlreadyExists_throwsException() {
        UUID userId = UUID.randomUUID();
        when(recruiterRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> companyService.createCompany(userId, "Bearer token", request))
                .isInstanceOf(RecruiterProfileAlreadyExistsException.class);

        verify(companyRepository, never()).save(any());
        verify(recruiterRepository, never()).saveAndFlush(any());
    }
}
