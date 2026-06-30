package com.prahlad.aijobportal.recruiterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Recruiter Service — owns Company, Recruiter Profile, and Company
 * Verification for the AI Job Portal platform.
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} so
 * shared beans from the {@code common} module (e.g. {@code CorrelationIdFilter})
 * are picked up automatically, mirroring the same approach used in
 * Auth Service and Candidate Service.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
@EnableFeignClients
public class RecruiterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruiterServiceApplication.class, args);
    }
}
