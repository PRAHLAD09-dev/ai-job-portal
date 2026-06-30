package com.prahlad.aijobportal.recruiterservice.asset.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.prahlad.aijobportal.recruiterservice.asset.exception.FileStorageException;
import com.prahlad.aijobportal.recruiterservice.asset.service.CloudinaryUploadResult;
import com.prahlad.aijobportal.recruiterservice.asset.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Cloudinary-backed {@link FileStorageService} implementation for
 * company logo/banner images. Unlike resumes (Candidate Service), these
 * are genuine images, so the default Cloudinary {@code resource_type=image}
 * applies — no {@code raw} override needed.
 *
 * Per DECISIONS.md (File Storage), only the resulting Cloudinary URL and
 * identifiers are ever persisted — file bytes are streamed directly to
 * Cloudinary and never written to local disk or stored as a database BLOB.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public CloudinaryUploadResult upload(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", folder,
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", false
            ));

            return new CloudinaryUploadResult(
                    (String) result.get("secure_url"),
                    (String) result.get("public_id"),
                    String.valueOf(result.get("format")),
                    file.getSize()
            );
        } catch (IOException ex) {
            log.error("Failed to upload company asset to Cloudinary", ex);
            throw new FileStorageException("Failed to upload image file. Please try again.");
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (IOException ex) {
            log.error("Failed to delete company asset [{}] from Cloudinary", publicId, ex);
            throw new FileStorageException("Failed to delete image file. Please try again.");
        }
    }
}
