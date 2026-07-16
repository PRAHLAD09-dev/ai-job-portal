package com.prahlad.aijobportal.applicationservice.application.dto.response;

import java.util.UUID;

/**
 * DAY11 "Apply Methods": tells the frontend how to route the candidate's
 * "Apply" click for a given job before it decides whether to render the
 * in-app apply form or redirect externally.
 */
public record ApplyInfoResponse(
        UUID jobId,
        String applyMethod,
        String externalApplyUrl
) {
}
