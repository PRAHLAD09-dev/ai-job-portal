package com.prahlad.aijobportal.candidateservice.resume.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.prahlad.aijobportal.candidateservice.resume.exception.FileStorageException;
import com.prahlad.aijobportal.candidateservice.resume.service.CloudinaryUploadResult;
import com.prahlad.aijobportal.candidateservice.resume.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Cloudinary-backed {@link FileStorageService} implementation for resume
 * files. Resumes are documents (PDF/DOC/DOCX), which Cloudinary classifies
 * as {@code raw} assets — both upload and destroy calls must specify
 * {@code resource_type=raw}, otherwise Cloudinary defaults to {@code image}
 * and the asset becomes unmanageable (uploads "succeed" into the wrong
 * bucket; deletes silently no-op).
 *
 * Per DECISIONS.md (File Storage), only the resulting Cloudinary URL and
 * identifiers are ever persisted — file bytes are streamed directly to
 * Cloudinary and never written to local disk or stored as a database BLOB.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryFileStorageService implements FileStorageService {

    private static final String RESOURCE_TYPE = "raw";

    private final Cloudinary cloudinary;

    @Override
    public CloudinaryUploadResult upload(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", RESOURCE_TYPE,
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
            log.error("Failed to upload resume to Cloudinary", ex);
            throw new FileStorageException("Failed to upload resume file. Please try again.");
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", RESOURCE_TYPE));
        } catch (IOException ex) {
            log.error("Failed to delete resume [{}] from Cloudinary", publicId, ex);
            throw new FileStorageException("Failed to delete resume file. Please try again.");
        }
    }
}
