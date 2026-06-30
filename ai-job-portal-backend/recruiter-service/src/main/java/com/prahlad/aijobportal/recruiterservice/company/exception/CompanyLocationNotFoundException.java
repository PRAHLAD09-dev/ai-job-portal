package com.prahlad.aijobportal.recruiterservice.company.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class CompanyLocationNotFoundException extends ResourceNotFoundException {

    public CompanyLocationNotFoundException(UUID id) {
        super("CompanyLocation", "id", id);
    }
}
