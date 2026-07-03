package com.prahlad.aijobportal.aiservice.coverletter.service.impl;

import com.prahlad.aijobportal.aiservice.coverletter.dto.request.CoverLetterRequest;
import com.prahlad.aijobportal.aiservice.coverletter.dto.response.CoverLetterResponse;
import com.prahlad.aijobportal.aiservice.coverletter.service.CoverLetterService;
import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.external.CandidateLookupService;
import com.prahlad.aijobportal.aiservice.external.JobLookupService;
import com.prahlad.aijobportal.aiservice.feign.dto.CandidateProfileSummaryResponse;
import com.prahlad.aijobportal.aiservice.feign.dto.JobDetailSummaryResponse;
import com.prahlad.aijobportal.aiservice.gemini.GeminiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * Generates a personalized cover letter for the authenticated candidate
 * applying to a given job. Unpersisted and stateless: cover letters are
 * a one-shot draft the candidate is expected to copy, edit, and use
 * client-side — there is no product need to store past generations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoverLetterServiceImpl implements CoverLetterService {

    private static final String PROMPT_TEMPLATE = """
            You are an expert career coach. Write a compelling, professional cover
            letter for the candidate below, applying to the job below. Keep it to
            3-4 short paragraphs, written in first person as the candidate, with no
            placeholder brackets left unfilled. Do not invent facts about the
            candidate beyond what is given.

            Candidate:
            Name: %s
            Headline: %s
            Summary: %s
            Skills: %s

            Job:
            Title: %s
            Company: %s
            Description: %s

            Additional notes from the candidate: %s

            Respond with the cover letter text only — no JSON, no markdown, no preamble.
            """;

    private final CandidateLookupService candidateLookupService;
    private final JobLookupService jobLookupService;
    private final GeminiClient geminiClient;

    @Override
    public CoverLetterResponse generate(String bearerToken, CoverLetterRequest request) {
        CandidateProfileSummaryResponse candidate = candidateLookupService.fetchCurrentCandidate(bearerToken);
        JobDetailSummaryResponse job = jobLookupService.fetchJob(request.jobId());

        String skills = candidate.skills() == null ? "" : candidate.skills().stream()
                .map(CandidateProfileSummaryResponse.SkillSummaryResponse::name)
                .collect(Collectors.joining(", "));

        String notes = StringUtils.hasText(request.additionalNotes()) ? request.additionalNotes() : "None";

        String prompt = PROMPT_TEMPLATE.formatted(
                candidate.fullName(), candidate.headline(), candidate.summary(), skills,
                job.title(), job.companyName(), job.description(), notes);

        String coverLetterText;
        try {
            coverLetterText = geminiClient.generateText(prompt).join().strip();
        } catch (Exception ex) {
            log.error("Cover letter generation failed for jobId={}", request.jobId(), ex);
            throw new AiGenerationException("Failed to generate cover letter", ex);
        }

        return new CoverLetterResponse(coverLetterText);
    }
}
