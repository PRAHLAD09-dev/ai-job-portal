package com.prahlad.aijobportal.aiservice.jobdescription.service.impl;

import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.jobdescription.dto.request.JobDescriptionRequest;
import com.prahlad.aijobportal.aiservice.jobdescription.dto.response.JobDescriptionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobDescriptionServiceImplTest {

    @Mock private AiStructuredResponseService aiStructuredResponseService;

    @InjectMocks
    private JobDescriptionServiceImpl jobDescriptionService;

    @Test
    void generate_delegatesToAiAndReturnsStructuredResult() {
        JobDescriptionRequest request = new JobDescriptionRequest(
                "Senior Backend Engineer", "FULL_TIME", "SENIOR",
                List.of("Owns the payments microservice", "Mentors junior engineers"));

        JobDescriptionResponse expected = new JobDescriptionResponse(
                "Responsibilities...\nQualifications...", List.of("Java", "Spring Boot", "Kafka"));

        when(aiStructuredResponseService.generateStructured(anyString(), eq(JobDescriptionResponse.class)))
                .thenReturn(expected);

        JobDescriptionResponse result = jobDescriptionService.generate(request);

        assertThat(result).isEqualTo(expected);
        verify(aiStructuredResponseService).generateStructured(anyString(), eq(JobDescriptionResponse.class));
    }
}
