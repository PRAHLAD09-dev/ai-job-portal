package com.prahlad.aijobportal.recruiterservice.feign;

import com.prahlad.aijobportal.recruiterservice.recruiter.exception.AuthServiceUnavailableException;
import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Translates Feign call failures against Auth Service, Job Service, and
 * Application Service (the three clients configured with
 * {@link FeignClientConfig}) into Recruiter Service's own exception
 * types, so the {@code GlobalExceptionHandler} here can produce a
 * consistent {@code ApiResponse} without leaking Feign's internal
 * exception types to the client.
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new ResourceNotFoundException("The requested resource could not be found (via " + methodKey + ")");
        }
        if (response.status() >= 500) {
            log.error("Downstream service call [{}] failed with status {}", methodKey, response.status());
            return new AuthServiceUnavailableException("A downstream service is temporarily unavailable. Please try again later.");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
