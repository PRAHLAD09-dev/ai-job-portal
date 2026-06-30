package com.prahlad.aijobportal.recruiterservice.asset.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when an uploaded logo/banner image fails validation —
 * unsupported format, exceeds the maximum allowed size, or is empty.
 */
public class InvalidImageFileException extends BusinessException {

    public InvalidImageFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_IMAGE_FILE");
    }
}
