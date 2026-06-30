package com.prahlad.aijobportal.recruiterservice.asset.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Provider-independent abstraction for company image asset storage
 * (logo, banner). Kept as an interface so the underlying provider
 * (Cloudinary today) could change without touching call sites.
 */
public interface FileStorageService {

    CloudinaryUploadResult upload(MultipartFile file, String folder);

    void delete(String publicId);
}
