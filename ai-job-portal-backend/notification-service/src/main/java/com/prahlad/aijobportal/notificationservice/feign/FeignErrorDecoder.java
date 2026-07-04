package com.prahlad.aijobportal.notificationservice.feign;

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
            return new ResourceNotFoundException("Requested user could not be found in Auth Service");
        }
        if (response.status() >= 500) {
            log.error("Auth Service call [{}] failed with status {}", methodKey, response.status());
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
