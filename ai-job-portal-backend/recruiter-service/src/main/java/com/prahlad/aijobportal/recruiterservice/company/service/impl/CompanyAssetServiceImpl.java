package com.prahlad.aijobportal.recruiterservice.company.service.impl;

import com.prahlad.aijobportal.recruiterservice.asset.event.CompanyAssetCleanupEvent;
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
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public CompanyAssetResponse upload(UUID userId, CompanyAssetType assetType, MultipartFile file) {
        imageFileValidator.validate(file);
        Company company = companyAccessGuard.resolveOwnedCompany(userId);

        String existingPublicId = currentPublicId(company, assetType);

        // Cloudinary upload has to happen before the DB write, since the
        // Resume/asset row needs the resulting URL/publicId to persist at
        // all. That ordering is unavoidable, but it means a DB failure
        // AFTER a successful upload would otherwise leave an orphaned,
        // permanently-billed Cloudinary asset with no application record
        // pointing to it. The try/catch below compensates for that.
        CloudinaryUploadResult uploadResult = fileStorageService.upload(file, folderFor(assetType));

        Company saved;
        try {
            applyAsset(company, assetType, uploadResult);
            saved = companyRepository.saveAndFlush(company);
        } catch (RuntimeException ex) {
            safeDeleteCompensation(uploadResult.publicId());
            throw ex;
        }

        if (existingPublicId != null) {
            // Deleting the OLD asset is deferred to AFTER_COMMIT rather
            // than done synchronously here: if this Cloudinary delete
            // call were to throw, it would otherwise roll back the DB
            // update we just made while the NEW asset we just uploaded
            // would be left orphaned anyway. Doing it after commit means
            // a delete failure only leaves the old asset as a harmless,
            // logged-for-cleanup orphan instead of corrupting an
            // otherwise successful upload.
            applicationEventPublisher.publishEvent(new CompanyAssetCleanupEvent(existingPublicId));
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

        Company saved;
        try {
            applyAsset(company, assetType, uploadResult);
            saved = companyRepository.saveAndFlush(company);
        } catch (RuntimeException ex) {
            // The DB update failed/rolled back - the newly-uploaded asset
            // is now unreferenced by any row. Compensate by deleting it;
            // the OLD asset is untouched and remains correctly referenced.
            safeDeleteCompensation(uploadResult.publicId());
            throw ex;
        }

        // Same AFTER_COMMIT rationale as upload(): a delete failure for
        // the old asset must not be able to roll back a successful
        // replace, nor leave the new asset orphaned.
        applicationEventPublisher.publishEvent(new CompanyAssetCleanupEvent(existingPublicId));

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

        // Same AFTER_COMMIT rationale: the DB is the source of truth for
        // "this asset no longer exists." Deleting the Cloudinary asset is
        // a best-effort cleanup that must not be able to roll back an
        // otherwise-successful DB update.
        applicationEventPublisher.publishEvent(new CompanyAssetCleanupEvent(existingPublicId));
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

    private void safeDeleteCompensation(String publicId) {
        try {
            fileStorageService.delete(publicId);
        } catch (Exception cleanupEx) {
            log.error("Failed to compensate/cleanup orphaned Cloudinary asset publicId={} after a failed "
                    + "database write; manual cleanup may be required", publicId, cleanupEx);
        }
    }
}
