package com.prahlad.aijobportal.candidateservice.resume.service;

/**
 * Internal result of a successful Cloudinary upload — the only pieces of
 * provider response needed to persist {@code Resume} metadata.
 */
public record CloudinaryUploadResult(
        String secureUrl,
        String publicId,
        String format,
        long bytes
) {
}
