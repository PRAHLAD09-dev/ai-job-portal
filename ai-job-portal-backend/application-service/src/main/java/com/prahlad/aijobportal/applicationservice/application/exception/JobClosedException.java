package com.prahlad.aijobportal.applicationservice.application.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class JobClosedException extends BusinessException {

    public JobClosedException() {
        super("This job is no longer accepting applications", HttpStatus.CONFLICT, "JOB_CLOSED");
    }
}
