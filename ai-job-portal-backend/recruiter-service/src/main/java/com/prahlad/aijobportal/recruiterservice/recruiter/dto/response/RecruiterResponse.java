package com.prahlad.aijobportal.recruiterservice.recruiter.dto.response;

import com.prahlad.aijobportal.recruiterservice.recruiter.enums.RecruiterTitle;

import java.util.UUID;

public record RecruiterResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        String phoneNumber,
        RecruiterTitle title,
        String designation,
        String profilePictureUrl,
        boolean owner,
        UUID companyId,
        String companyName
) {
}
