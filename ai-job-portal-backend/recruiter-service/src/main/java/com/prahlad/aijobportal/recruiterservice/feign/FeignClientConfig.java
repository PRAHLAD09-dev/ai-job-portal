package com.prahlad.aijobportal.recruiterservice.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/**
 * Per-client Feign configuration for {@link AuthServiceClient}, registered
 * via {@code @FeignClient(configuration = FeignClientConfig.class)}.
 */
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
