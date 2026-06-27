package com.prahlad.aijobportal.apigateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global reactive filter that establishes the correlation ID for every
 * request entering the platform through the Gateway.
 *
 * If the client already supplied X-Correlation-Id (uncommon, but supported
 * for advanced clients/tracing tools), it is preserved; otherwise a new one
 * is generated here, since the Gateway is the platform's front door.
 * The header is added to the request before it is forwarded downstream, so
 * every microservice's own correlation filter (see common module) just
 * picks up the existing value instead of generating a new one — keeping
 * one correlation ID per request across the entire call chain.
 *
 * Note: this is a Gateway-specific reactive implementation (WebFlux),
 * distinct from the servlet-based CorrelationIdFilter in the shared common
 * module, which is used by the (non-reactive) business microservices.
 */
@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);

        MDC.put(CORRELATION_ID_MDC_KEY, finalCorrelationId);
        try {
            return chain.filter(mutatedExchange);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
