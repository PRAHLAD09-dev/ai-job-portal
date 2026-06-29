package com.prahlad.aijobportal.candidateservice.resume.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Provider-independent abstraction for resume file storage. Kept as an
 * interface so the underlying provider (Cloudinary today) could change
 * without touching call sites, mirroring the same provider-independence
 * principle DECISIONS.md applies to the AI integration.
 */
public interface FileStorageService {

    CloudinaryUploadResult upload(MultipartFile file, String folder);

    void delete(String publicId);
}
