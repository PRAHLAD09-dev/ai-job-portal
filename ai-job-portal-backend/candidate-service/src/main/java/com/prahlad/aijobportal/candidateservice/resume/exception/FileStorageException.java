package com.prahlad.aijobportal.candidateservice.resume.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when the Cloudinary upload or delete operation fails (network
 * error, provider-side error, or any other failure of the underlying
 * file-storage provider).
 */
public class FileStorageException extends BusinessException {

    public FileStorageException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "FILE_STORAGE_ERROR");
    }
}
