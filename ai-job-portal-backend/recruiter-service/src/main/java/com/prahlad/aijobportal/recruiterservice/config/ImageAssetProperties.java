package com.prahlad.aijobportal.recruiterservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Binds {@code app.company-asset.*} configuration properties: upload
 * constraints for company logo/banner images.
 */
@Configuration
@ConfigurationProperties(prefix = "app.company-asset")
@Getter
@Setter
public class ImageAssetProperties {

    private long maxFileSizeBytes;

    private List<String> allowedFormats;

    private String logoCloudinaryFolder;

    private String bannerCloudinaryFolder;
}
