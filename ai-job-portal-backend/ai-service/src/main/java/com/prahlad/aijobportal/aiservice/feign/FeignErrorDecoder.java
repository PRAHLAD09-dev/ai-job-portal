package com.prahlad.aijobportal.aiservice.feign;

import com.prahlad.aijobportal.aiservice.exception.DependencyServiceUnavailableException;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new ResourceNotFoundException("Requested resource could not be found in the dependency service");
        }
        if (response.status() >= 500) {
            log.error("Dependency service call [{}] failed with status {}", methodKey, response.status());
            return new DependencyServiceUnavailableException("A dependency service is temporarily unavailable. Please try again later.");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
