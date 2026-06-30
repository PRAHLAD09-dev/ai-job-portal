package com.prahlad.aijobportal.recruiterservice.asset.util;

import com.prahlad.aijobportal.recruiterservice.asset.exception.InvalidImageFileException;
import com.prahlad.aijobportal.recruiterservice.config.ImageAssetProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates an uploaded logo/banner image against the configured size
 * and format constraints before it is forwarded to Cloudinary.
 */
@Component
@RequiredArgsConstructor
public class ImageFileValidator {

    private final ImageAssetProperties imageAssetProperties;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageFileException("Image file must not be empty");
        }

        if (file.getSize() > imageAssetProperties.getMaxFileSizeBytes()) {
            throw new InvalidImageFileException(
                    "Image file exceeds the maximum allowed size of "
                            + (imageAssetProperties.getMaxFileSizeBytes() / (1024 * 1024)) + " MB");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(extension) || !imageAssetProperties.getAllowedFormats().contains(extension.toLowerCase())) {
            throw new InvalidImageFileException(
                    "Unsupported image file format. Allowed formats: " + imageAssetProperties.getAllowedFormats());
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
