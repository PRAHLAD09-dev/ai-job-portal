package com.prahlad.aijobportal.aiservice.jobdescription.service.impl;

import com.prahlad.aijobportal.aiservice.gemini.AiStructuredResponseService;
import com.prahlad.aijobportal.aiservice.jobdescription.dto.request.JobDescriptionRequest;
import com.prahlad.aijobportal.aiservice.jobdescription.dto.response.JobDescriptionResponse;
import com.prahlad.aijobportal.aiservice.jobdescription.service.JobDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Drafts a job description and required-skills list from a few
 * recruiter-supplied bullet points — used before the job is created in
 * Job Service (this service never reads or writes Job Service data for
 * this feature, hence no {@code JobLookupService} dependency here).
 */
@Service
@RequiredArgsConstructor
public class JobDescriptionServiceImpl implements JobDescriptionService {

    private static final String PROMPT_TEMPLATE = """
            You are an expert technical recruiter and copywriter. Draft a
            professional job description from the details below. Return a JSON
            object with these exact fields:
            - description: a well-structured job description (responsibilities, qualifications) as a single string with newline characters between sections
            - requiredSkills: an array of short strings naming the key skills required for this role

            Job title: %s
            Job type: %s
            Experience level: %s
            Key points provided by the recruiter: %s
            """;

    private final AiStructuredResponseService aiStructuredResponseService;

    @Override
    public JobDescriptionResponse generate(JobDescriptionRequest request) {
        String keyPoints = String.join("; ", request.keyPoints());

        return aiStructuredResponseService.generateStructured(
                PROMPT_TEMPLATE.formatted(request.jobTitle(), request.jobType(), request.experienceLevel(), keyPoints),
                JobDescriptionResponse.class);
    }
}
