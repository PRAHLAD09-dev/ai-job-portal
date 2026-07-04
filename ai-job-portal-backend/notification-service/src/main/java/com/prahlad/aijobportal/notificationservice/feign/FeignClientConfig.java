package com.prahlad.aijobportal.notificationservice.feign;

import com.prahlad.aijobportal.common.constant.CommonConstants;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Attaches the shared internal-service token to every outgoing Feign
 * call made by this service, per DECISIONS.md (Internal JWT for
 * Service-to-Service Communication). Kafka listeners run with no
 * incoming HTTP request or bearer token, so this service cannot forward
 * a user token like every other Feign caller in the platform does; it
 * authenticates as the Notification Service itself via a shared secret
 * validated by Auth Service InternalServiceAuthFilter instead.
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
