package com.prahlad.aijobportal.recruiterservice.company.enums;

/**
 * Distinguishes which Cloudinary-backed image asset a Company owns —
 * used by the asset upload/replace/delete pipeline shared between logo
 * and banner.
 */
public enum CompanyAssetType {
    LOGO,
    BANNER
}
