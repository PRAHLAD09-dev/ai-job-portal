package com.prahlad.aijobportal.aiservice.resumeanalysis.service.impl;

import com.prahlad.aijobportal.aiservice.event.AiEventPublisher;
import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.ResumeAnalysisAiResult;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.request.AnalyzeResumeRequest;
import com.prahlad.aijobportal.aiservice.resumeanalysis.dto.response.ResumeAnalysisResponse;
import com.prahlad.aijobportal.aiservice.resumeanalysis.entity.ResumeAnalysis;
import com.prahlad.aijobportal.aiservice.resumeanalysis.mapper.ResumeAnalysisMapper;
import com.prahlad.aijobportal.aiservice.resumeanalysis.repository.ResumeAnalysisRepository;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ResumeTextExtractionService;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeAnalysisServiceImplTest {

    @Mock private ResumeAnalysisRepository resumeAnalysisRepository;
    @Mock private AiStructuredResponseService aiStructuredResponseService;
    @Mock private AiEventPublisher aiEventPublisher;
    @Mock private ResumeTextExtractionService resumeTextExtractionService;

    private ResumeAnalysisMapper resumeAnalysisMapper;
    private ResumeAnalysisServiceImpl resumeAnalysisService;

    private UUID candidateId;
    private AnalyzeResumeRequest request;

    @BeforeEach
    void setUp() {
        resumeAnalysisMapper = new ResumeAnalysisMapperImplForTest();
        resumeAnalysisService = new ResumeAnalysisServiceImpl(
                resumeAnalysisRepository, resumeAnalysisMapper, aiStructuredResponseService, aiEventPublisher,
                resumeTextExtractionService);

        candidateId = UUID.randomUUID();
        request = new AnalyzeResumeRequest("https://cloudinary.com/resume.pdf");
        when(resumeTextExtractionService.extractText(anyString())).thenReturn("5 years Java experience");
    }

    @Test
    void analyze_createsNewAnalysis_whenNoDuplicateExists() {
        when(resumeAnalysisRepository.findTopByCandidateIdAndResumeTextHashOrderByCreatedAtDesc(any(), anyString()))
                .thenReturn(Optional.empty());

        ResumeAnalysisAiResult aiResult = new ResumeAnalysisAiResult(
                85, List.of("Strong Java skills"), List.of("No cloud experience"),
                List.of("Kubernetes"), List.of("Add a projects section"),
                "Backend engineer with 5 years of Java experience.",
                List.of("Inventory service - Spring Boot, Kafka"),
                List.of("AWS Certified Developer"),
                List.of("English"),
                List.of("Reduced API latency by 30%"));
        when(aiStructuredResponseService.generateStructured(anyString(), org.mockito.ArgumentMatchers.eq(ResumeAnalysisAiResult.class)))
                .thenReturn(aiResult);

        when(resumeAnalysisRepository.save(any(ResumeAnalysis.class))).thenAnswer(invocation -> {
            ResumeAnalysis entity = invocation.getArgument(0);
            entity.setId(UUID.randomUUID());
            entity.setCreatedAt(Instant.now());
            return entity;
        });

        ResumeAnalysisResponse response = resumeAnalysisService.analyze(candidateId, request);

        assertThat(response.atsScore()).isEqualTo(85);
        assertThat(response.strengths()).containsExactly("Strong Java skills");
        assertThat(response.professionalSummary()).isEqualTo("Backend engineer with 5 years of Java experience.");
        assertThat(response.projects()).containsExactly("Inventory service - Spring Boot, Kafka");
        assertThat(response.certifications()).containsExactly("AWS Certified Developer");
        assertThat(response.languages()).containsExactly("English");
        assertThat(response.achievements()).containsExactly("Reduced API latency by 30%");

        ArgumentCaptor<ResumeAnalysis> captor = ArgumentCaptor.forClass(ResumeAnalysis.class);
        verify(resumeAnalysisRepository).save(captor.capture());
        assertThat(captor.getValue().getCandidateId()).isEqualTo(candidateId);
        assertThat(captor.getValue().getAtsScore()).isEqualTo(85);

        verify(aiEventPublisher).publishResumeAnalyzed(any());
        verify(aiEventPublisher).publishAtsCompleted(any());
    }

    @Test
    void analyze_reusesExistingAnalysis_whenDuplicateResumeTextExists() {
        ResumeAnalysis existing = ResumeAnalysis.builder()
                .id(UUID.randomUUID())
                .candidateId(candidateId)
                .resumeUrl(request.resumeUrl())
                .resumeText("5 years Java experience")
                .atsScore(70)
                .strengths("Good communication")
                .build();
        existing.setCreatedAt(Instant.now());

        when(resumeAnalysisRepository.findTopByCandidateIdAndResumeTextHashOrderByCreatedAtDesc(any(), anyString()))
                .thenReturn(Optional.of(existing));

        ResumeAnalysisResponse response = resumeAnalysisService.analyze(candidateId, request);

        assertThat(response.atsScore()).isEqualTo(70);
        verify(resumeAnalysisRepository, never()).save(any());
        verify(aiStructuredResponseService, never()).generateStructured(anyString(), any());
        verify(aiEventPublisher, never()).publishResumeAnalyzed(any());
    }

    @Test
    void getLatestForCandidate_throwsNotFound_whenNoAnalysisExists() {
        when(resumeAnalysisRepository.findTopByCandidateIdOrderByCreatedAtDesc(candidateId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeAnalysisService.getLatestForCandidate(candidateId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * Hand-written stand-in for the MapStruct-generated implementation
     * (annotation processing does not run for this test source set),
     * mirroring exactly what {@link ResumeAnalysisMapper}'s generated
     * class would produce.
     */
    private static class ResumeAnalysisMapperImplForTest implements ResumeAnalysisMapper {
        @Override
        public ResumeAnalysisResponse toResponse(ResumeAnalysis entity) {
            if (entity == null) {
                return null;
            }
            return new ResumeAnalysisResponse(
                    entity.getId(), entity.getCandidateId(), entity.getResumeUrl(), entity.getAtsScore(),
                    toList(entity.getStrengths()), toList(entity.getWeaknesses()),
                    toList(entity.getMissingSkills()), toList(entity.getRecommendations()), entity.getCreatedAt(),
                    null, null, null, null, null);
        }
    }
}
