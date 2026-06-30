package com.prahlad.aijobportal.recruiterservice.recruiter.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class RecruiterProfileAlreadyExistsException extends BusinessException {

    public RecruiterProfileAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, "RECRUITER_PROFILE_ALREADY_EXISTS");
    }
}
