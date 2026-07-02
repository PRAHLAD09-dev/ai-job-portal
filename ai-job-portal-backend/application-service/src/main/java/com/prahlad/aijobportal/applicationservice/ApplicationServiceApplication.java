package com.prahlad.aijobportal.applicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Application Service — owns Job Applications, Application Timeline,
 * and the hiring workflow for the AI Job Portal platform. Does not
 * manage Jobs or Candidate Profiles.
 *
 * Component scanning is rooted at {@code com.prahlad.aijobportal} so
 * shared beans from the {@code common} module are picked up
 * automatically, mirroring Auth/Candidate/Recruiter/Job Service.
 */
@SpringBootApplication(scanBasePackages = "com.prahlad.aijobportal")
@EnableDiscoveryClient
@EnableFeignClients
public class ApplicationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationServiceApplication.class, args);
    }
}
