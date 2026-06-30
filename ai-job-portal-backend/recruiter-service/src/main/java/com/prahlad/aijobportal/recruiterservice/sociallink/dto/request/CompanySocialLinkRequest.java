package com.prahlad.aijobportal.recruiterservice.sociallink.dto.request;

import com.prahlad.aijobportal.recruiterservice.sociallink.enums.SocialPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompanySocialLinkRequest(

        @NotNull(message = "Platform is required")
        SocialPlatform platform,

        @NotBlank(message = "URL is required")
        @Size(max = 500, message = "URL must not exceed 500 characters")
        String url
) {
}
