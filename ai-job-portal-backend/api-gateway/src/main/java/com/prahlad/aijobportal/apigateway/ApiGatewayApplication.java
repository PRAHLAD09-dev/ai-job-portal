package com.prahlad.aijobportal.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway - single entry point for the React frontend into the
 * AI Job Portal microservices platform.
 *
 * Responsibilities owned at this layer:
 * - Routing requests to downstream services via Eureka service discovery
 * - Correlation ID generation/propagation (reactive equivalent of the
 *   servlet-based filter in the common module)
 * - Redis-backed request rate limiting
 * - Resilience4j circuit breaking for downstream calls
 * - CORS handling for the frontend origin
 *
 * Authentication/authorization (JWT validation) is intentionally not part
 * of this phase and will be added during Auth Service development.
 *
 * Business routes are added one at a time as each microservice is
 * completed, per the project's development order.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
