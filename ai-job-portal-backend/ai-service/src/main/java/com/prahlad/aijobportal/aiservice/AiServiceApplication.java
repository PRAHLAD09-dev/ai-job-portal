package com.prahlad.aijobportal.aiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * AI Service — owns Resume Analysis, ATS Scoring, Job/Candidate
 * Recommendations, Skill Gap Analysis, Interview Question Generation,
 * Cover Letter Generation, and Job Description Generation for the AI
 * Job Portal platform, backed by the Gemini API (DAY07_AI_SERVICE.md).
 * Does not manage Authentication, Jobs, or Companies.
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} so
 * shared beans from the {@code common} module are picked up
 * automatically, mirroring every other business service.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
@EnableFeignClients
public class AiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
    }
}
