package com.prahlad.aijobportal.recruiterservice.company.exception;

import com.prahlad.aijobportal.common.exception.ResourceConflictException;

public class CompanySlugAlreadyExistsException extends ResourceConflictException {

    public CompanySlugAlreadyExistsException(String slug) {
        super("A company with the identifier '" + slug + "' already exists");
    }
}
