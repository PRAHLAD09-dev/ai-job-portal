package com.prahlad.aijobportal.candidateservice.candidate.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a user who already has a candidate profile attempts to
 * create a second one. Each Auth Service user may own exactly one
 * Candidate profile.
 */
public class CandidateProfileAlreadyExistsException extends BusinessException {

    public CandidateProfileAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, "CANDIDATE_PROFILE_ALREADY_EXISTS");
    }
}
