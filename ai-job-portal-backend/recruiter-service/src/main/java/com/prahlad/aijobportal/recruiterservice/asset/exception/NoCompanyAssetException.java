package com.prahlad.aijobportal.recruiterservice.asset.exception;

import com.prahlad.aijobportal.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when attempting to replace or delete a company logo/banner
 * that has not been uploaded yet.
 */
public class NoCompanyAssetException extends BusinessException {

    public NoCompanyAssetException(String message) {
        super(message, HttpStatus.NOT_FOUND, "NO_COMPANY_ASSET");
    }
}
