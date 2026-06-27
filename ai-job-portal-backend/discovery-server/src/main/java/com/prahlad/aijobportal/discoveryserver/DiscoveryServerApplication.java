package com.prahlad.aijobportal.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Discovery Server.
 * Acts as the service registry for the entire AI Job Portal platform.
 * Every microservice (Config Server excluded, since it must be available
 * before discovery is needed at bootstrap) registers itself here on
 * startup, and the API Gateway + OpenFeign clients use this registry to
 * resolve service instances dynamically instead of hardcoded URLs.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
