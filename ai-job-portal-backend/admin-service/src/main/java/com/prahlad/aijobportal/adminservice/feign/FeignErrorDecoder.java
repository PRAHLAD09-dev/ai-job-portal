package com.prahlad.aijobportal.adminservice.feign;

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
            return new ResourceNotFoundException("The requested resource could not be found in the downstream service");
        }
        if (response.status() >= 500) {
            log.error("Downstream call [{}] failed with status {}", methodKey, response.status());
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
