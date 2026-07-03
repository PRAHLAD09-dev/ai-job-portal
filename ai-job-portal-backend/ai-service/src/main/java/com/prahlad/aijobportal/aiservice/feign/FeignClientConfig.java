package com.prahlad.aijobportal.aiservice.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
