package com.prahlad.aijobportal.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * Resolves the key used to bucket requests for Redis-backed rate limiting.
 *
 * For now, requests are limited per client IP address, since no
 * authenticated principal is available at the Gateway in this phase.
 * Per-user rate limiting can be introduced later once an authenticated
 * principal is available at the Gateway, without changing this filter's
 * wiring elsewhere.
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            String ip = (remoteAddress != null && remoteAddress.getAddress() != null)
                    ? remoteAddress.getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }
}
