package com.prahlad.aijobportal.recruiterservice.sociallink.dto.response;

import com.prahlad.aijobportal.recruiterservice.sociallink.enums.SocialPlatform;

import java.util.UUID;

public record CompanySocialLinkResponse(
        UUID id,
        SocialPlatform platform,
        String url
) {
}
