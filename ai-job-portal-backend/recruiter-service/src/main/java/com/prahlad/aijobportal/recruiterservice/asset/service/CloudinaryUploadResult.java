package com.prahlad.aijobportal.recruiterservice.asset.service;

/**
 * Internal result of a successful Cloudinary upload — the only pieces of
 * provider response needed to persist Company asset metadata.
 */
public record CloudinaryUploadResult(
        String secureUrl,
        String publicId,
        String format,
        long bytes
) {
}
