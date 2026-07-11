package com.prahlad.aijobportal.recruiterservice.asset.event;

/**
 * Raised when a Cloudinary asset (identified by its {@code publicId}) is
 * no longer referenced by any DB row and should be deleted - e.g. the
 * old logo/banner after {@code replace()}/{@code upload()}, or the
 * asset after {@code delete()}. Handled by
 * {@link CompanyAssetCleanupEventListener} only after the enclosing
 * transaction commits, so the DB stays the source of truth: a Cloudinary
 * failure here never rolls back a DB change, and a DB rollback never
 * triggers a delete for an asset that's still referenced.
 */
public record CompanyAssetCleanupEvent(String publicId) {
}
