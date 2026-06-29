package com.prahlad.aijobportal.candidateservice.candidate.exception;

import com.prahlad.aijobportal.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class CandidateNotFoundException extends ResourceNotFoundException {

    public CandidateNotFoundException(UUID userId) {
        super("Candidate profile not found for the authenticated user. Please create your profile first.");
    }

    public static CandidateNotFoundException forUser(UUID userId) {
        return new CandidateNotFoundException(userId);
    }
}
