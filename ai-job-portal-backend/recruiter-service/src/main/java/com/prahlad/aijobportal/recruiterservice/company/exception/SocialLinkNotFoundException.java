package com.prahlad.aijobportal.recruiterservice.company.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class SocialLinkNotFoundException extends ResourceNotFoundException {

    public SocialLinkNotFoundException(UUID id) {
        super("CompanySocialLink", "id", id);
    }
}
