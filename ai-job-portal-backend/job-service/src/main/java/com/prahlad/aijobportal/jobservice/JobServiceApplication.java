package com.prahlad.aijobportal.jobservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Job Service — owns Jobs, Job Categories, Saved Jobs, and Job Alerts
 * for the AI Job Portal platform. Does not manage Applications or
 * Interviews (a future Application Service phase).
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} so
 * shared beans from the {@code common} module are picked up
 * automatically, mirroring Auth/Candidate/Recruiter Service.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
@EnableFeignClients
public class JobServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobServiceApplication.class, args);
    }
}
