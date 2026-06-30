package com.prahlad.aijobportal.recruiterservice.company.service.impl;

import com.prahlad.aijobportal.recruiterservice.asset.exception.NoCompanyAssetException;
import com.prahlad.aijobportal.recruiterservice.asset.service.CloudinaryUploadResult;
import com.prahlad.aijobportal.recruiterservice.asset.service.FileStorageService;
import com.prahlad.aijobportal.recruiterservice.asset.util.ImageFileValidator;
import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyAssetResponse;
import com.prahlad.aijobportal.recruiterservice.company.entity.Company;
import com.prahlad.aijobportal.recruiterservice.company.enums.CompanyAssetType;
import com.prahlad.aijobportal.recruiterservice.company.repository.CompanyRepository;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyAccessGuard;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyAssetService;
import com.prahlad.aijobportal.recruiterservice.config.ImageAssetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyAssetServiceImpl implements CompanyAssetService {

    private final CompanyRepository companyRepository;
    private final CompanyAccessGuard companyAccessGuard;
    private final FileStorageService fileStorageService;
    private final ImageFileValidator imageFileValidator;
    private final ImageAssetProperties imageAssetProperties;

    @Override
    @Transactional
    public CompanyAssetResponse upload(UUID userId, CompanyAssetType assetType, MultipartFile file) {
        imageFileValidator.validate(file);
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        String existingPublicId = currentPublicId(company, assetType);
        CloudinaryUploadResult uploadResult = fileStorageService.upload(file, folderFor(assetType));

        applyAsset(company, assetType, uploadResult);
        Company saved = companyRepository.save(company);

        if (existingPublicId != null) {
            fileStorageService.delete(existingPublicId);
        }

        log.info("Uploaded {} for companyId={}", assetType, company.getId());
        return new CompanyAssetResponse(assetType.name(), urlFor(saved, assetType));
    }

    @Override
    @Transactional
    public CompanyAssetResponse replace(UUID userId, CompanyAssetType assetType, MultipartFile file) {
        imageFileValidator.validate(file);
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        String existingPublicId = currentPublicId(company, assetType);
        if (existingPublicId == null) {
            throw new NoCompanyAssetException("No " + assetType.name().toLowerCase() + " exists yet to replace. Upload one first.");
        }

        CloudinaryUploadResult uploadResult = fileStorageService.upload(file, folderFor(assetType));
        applyAsset(company, assetType, uploadResult);
        Company saved = companyRepository.save(company);

        fileStorageService.delete(existingPublicId);

        log.info("Replaced {} for companyId={}", assetType, company.getId());
        return new CompanyAssetResponse(assetType.name(), urlFor(saved, assetType));
    }

    @Override
    @Transactional
    public void delete(UUID userId, CompanyAssetType assetType) {
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        String existingPublicId = currentPublicId(company, assetType);
        if (existingPublicId == null) {
            throw new NoCompanyAssetException("No " + assetType.name().toLowerCase() + " exists to delete.");
        }

        clearAsset(company, assetType);
        companyRepository.save(company);

        fileStorageService.delete(existingPublicId);
        log.info("Deleted {} for companyId={}", assetType, company.getId());
    }

    private String currentPublicId(Company company, CompanyAssetType assetType) {
        return assetType == CompanyAssetType.LOGO
                ? company.getLogoCloudinaryPublicId()
                : company.getBannerCloudinaryPublicId();
    }

    private String urlFor(Company company, CompanyAssetType assetType) {
        return assetType == CompanyAssetType.LOGO ? company.getLogoUrl() : company.getBannerUrl();
    }

    private String folderFor(CompanyAssetType assetType) {
        return assetType == CompanyAssetType.LOGO
                ? imageAssetProperties.getLogoCloudinaryFolder()
                : imageAssetProperties.getBannerCloudinaryFolder();
    }

    private void applyAsset(Company company, CompanyAssetType assetType, CloudinaryUploadResult uploadResult) {
        if (assetType == CompanyAssetType.LOGO) {
            company.setLogoUrl(uploadResult.secureUrl());
            company.setLogoCloudinaryPublicId(uploadResult.publicId());
        } else {
            company.setBannerUrl(uploadResult.secureUrl());
            company.setBannerCloudinaryPublicId(uploadResult.publicId());
        }
    }

    private void clearAsset(Company company, CompanyAssetType assetType) {
        if (assetType == CompanyAssetType.LOGO) {
            company.setLogoUrl(null);
            company.setLogoCloudinaryPublicId(null);
        } else {
            company.setBannerUrl(null);
            company.setBannerCloudinaryPublicId(null);
        }
    }
}
