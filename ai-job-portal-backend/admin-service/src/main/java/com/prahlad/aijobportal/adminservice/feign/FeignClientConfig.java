package com.prahlad.aijobportal.adminservice.feign;

import com.prahlad.aijobportal.common.constant.CommonConstants;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Attaches the shared internal-service token to every outgoing Feign
 * call made by Admin Service, per DECISIONS.md (Internal JWT for
 * Service-to-Service Communication). Mirrors the identical
 * {@code FeignClientConfig} pattern already established in
 * Notification Service: Admin Service authenticates to every downstream
 * service's internal-only admin endpoints as itself, via a shared
 * secret validated by each service's {@code InternalServiceAuthFilter}
 * — it never forwards the caller's own user bearer token, since a
 * platform administrator's token was never meant to be a Recruiter/Job/
 * Application/AI/Notification Service credential.
 */
public class FeignClientConfig {

    @Value("${app.internal.service-token}")
    private String internalServiceToken;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    public RequestInterceptor internalServiceTokenInterceptor() {
        return requestTemplate -> requestTemplate.header(
                CommonConstants.INTERNAL_SERVICE_TOKEN_HEADER, internalServiceToken);
    }
}
