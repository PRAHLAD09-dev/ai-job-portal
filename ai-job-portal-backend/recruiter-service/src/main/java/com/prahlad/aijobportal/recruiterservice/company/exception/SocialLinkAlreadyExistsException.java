package com.prahlad.aijobportal.recruiterservice.company.exception;

import com.prahlad.aijobportal.common.exception.ResourceConflictException;

public class SocialLinkAlreadyExistsException extends ResourceConflictException {

    public SocialLinkAlreadyExistsException(String platform) {
        super("A social link for platform '" + platform + "' already exists for this company");
    }
}
