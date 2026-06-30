package com.prahlad.aijobportal.recruiterservice.feign;

import com.prahlad.aijobportal.recruiterservice.recruiter.exception.AuthServiceUnavailableException;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Translates Feign call failures against Auth Service into the
 * Recruiter Service's own exception types, so the {@code GlobalExceptionHandler}
 * here can produce a consistent {@code ApiResponse} without leaking
 * Feign's internal exception types to the client.
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new ResourceNotFoundException("Authenticated user could not be found in Auth Service");
        }
        if (response.status() >= 500) {
            log.error("Auth Service call [{}] failed with status {}", methodKey, response.status());
            return new AuthServiceUnavailableException("Auth Service is temporarily unavailable. Please try again later.");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
