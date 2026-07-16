package com.prahlad.aijobportal.recruiterservice.feign;

import com.prahlad.aijobportal.common.constant.CommonConstants;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Feign configuration for clients calling another service's
 * internal-only {@code /*}{@code /internal/**} endpoints (as opposed to
 * {@link FeignClientConfig}, used for clients that forward the caller's
 * own user bearer token to an already-authenticated {@code /me}
 * endpoint). Attaches the shared internal-service token to every
 * outgoing call, per DECISIONS.md ("Internal JWT for Service-to-Service
 * Communication") — mirrors the identical pattern already established
 * in Admin Service's {@code FeignClientConfig}.
 */
public class InternalFeignClientConfig {

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
