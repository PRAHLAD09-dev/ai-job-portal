package com.prahlad.aijobportal.candidateservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Candidate Service — owns Candidate Profile, Resume, Education,
 * Experience, Skills, Projects, Certifications, Portfolio, and Social
 * Links for the AI Job Portal platform.
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} so
 * shared beans from the {@code common} module (e.g. {@code CorrelationIdFilter})
 * are picked up automatically, mirroring the same approach used in
 * Auth Service.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
@EnableFeignClients
public class CandidateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CandidateServiceApplication.class, args);
    }
}
