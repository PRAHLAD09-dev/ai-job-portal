package com.prahlad.aijobportal.candidateservice.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the singleton Cloudinary client used by the Resume feature
 * for uploading/deleting resume files. Per DECISIONS.md (File Storage),
 * only the resulting URLs are ever persisted — never file bytes.
 */
@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    private final CloudinaryProperties cloudinaryProperties;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinaryProperties.getCloudName(),
                "api_key", cloudinaryProperties.getApiKey(),
                "api_secret", cloudinaryProperties.getApiSecret(),
                "secure", true
        ));
    }
}
