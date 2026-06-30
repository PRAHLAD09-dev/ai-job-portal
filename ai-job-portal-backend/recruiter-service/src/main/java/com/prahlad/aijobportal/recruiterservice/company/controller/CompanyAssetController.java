package com.prahlad.aijobportal.recruiterservice.company.controller;

import com.prahlad.aijobportal.recruiterservice.company.dto.response.CompanyAssetResponse;
import com.prahlad.aijobportal.recruiterservice.company.enums.CompanyAssetType;
import com.prahlad.aijobportal.recruiterservice.company.service.CompanyAssetService;
import com.prahlad.aijobportal.recruiterservice.security.principal.AuthenticatedUser;
import com.prahlad.aijobportal.common.constant.CommonConstants;
import com.prahlad.aijobportal.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Logo and banner upload/replace/delete for the authenticated recruiter's
 * own company. Both asset types share the same upload mechanics
 * ({@link CompanyAssetService}); only the target field and Cloudinary
 * folder differ.
 */
@RestController
@RequestMapping(CommonConstants.API_BASE_PATH + "/companies/me/assets")
@RequiredArgsConstructor
@Tag(name = "Company Assets", description = "Company logo and banner upload, replace, and delete")
public class CompanyAssetController {

    private final CompanyAssetService companyAssetService;

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload the authenticated recruiter's company logo")
    public ResponseEntity<ApiResponse<CompanyAssetResponse>> uploadLogo(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("file") MultipartFile file) {
        CompanyAssetResponse response = companyAssetService.upload(principal.userId(), CompanyAssetType.LOGO, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Logo uploaded successfully", response));
    }

    @PutMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Replace the authenticated recruiter's company logo")
    public ResponseEntity<ApiResponse<CompanyAssetResponse>> replaceLogo(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("file") MultipartFile file) {
        CompanyAssetResponse response = companyAssetService.replace(principal.userId(), CompanyAssetType.LOGO, file);
        return ResponseEntity.ok(ApiResponse.success("Logo replaced successfully", response));
    }

    @DeleteMapping("/logo")
    @Operation(summary = "Delete the authenticated recruiter's company logo")
    public ResponseEntity<ApiResponse<Void>> deleteLogo(@AuthenticationPrincipal AuthenticatedUser principal) {
        companyAssetService.delete(principal.userId(), CompanyAssetType.LOGO);
        return ResponseEntity.ok(ApiResponse.success("Logo deleted successfully", null));
    }

    @PostMapping(value = "/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload the authenticated recruiter's company banner")
    public ResponseEntity<ApiResponse<CompanyAssetResponse>> uploadBanner(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("file") MultipartFile file) {
        CompanyAssetResponse response = companyAssetService.upload(principal.userId(), CompanyAssetType.BANNER, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Banner uploaded successfully", response));
    }

    @PutMapping(value = "/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Replace the authenticated recruiter's company banner")
    public ResponseEntity<ApiResponse<CompanyAssetResponse>> replaceBanner(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("file") MultipartFile file) {
        CompanyAssetResponse response = companyAssetService.replace(principal.userId(), CompanyAssetType.BANNER, file);
        return ResponseEntity.ok(ApiResponse.success("Banner replaced successfully", response));
    }

    @DeleteMapping("/banner")
    @Operation(summary = "Delete the authenticated recruiter's company banner")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@AuthenticationPrincipal AuthenticatedUser principal) {
        companyAssetService.delete(principal.userId(), CompanyAssetType.BANNER);
        return ResponseEntity.ok(ApiResponse.success("Banner deleted successfully", null));
    }
}
