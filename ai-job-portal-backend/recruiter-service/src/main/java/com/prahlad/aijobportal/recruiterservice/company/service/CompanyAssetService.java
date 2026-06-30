package com.prahlad.aijobportal.recruiterservice.company.service;

import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyAssetResponse;
import com.prahlad.aijobportal.recruiterservice.company.enums.CompanyAssetType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Manages upload/replace/delete of a {@code Company}'s logo and banner
 * images. Shared between both asset types since the upload/replace/delete
 * mechanics are identical — only the target field and Cloudinary folder
 * differ (per {@link CompanyAssetType}).
 */
public interface CompanyAssetService {

    CompanyAssetResponse upload(UUID userId, CompanyAssetType assetType, MultipartFile file);

    CompanyAssetResponse replace(UUID userId, CompanyAssetType assetType, MultipartFile file);

    void delete(UUID userId, CompanyAssetType assetType);
}
