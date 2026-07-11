package com.prahlad.aijobportal.candidateservice.resume.event;

/**
 * Raised when a Cloudinary asset (identified by its {@code publicId}) is
 * no longer referenced by any DB row and should be deleted - e.g. the
 * old file after {@code replace()}, or the file after {@code delete()}.
 * Handled by {@link ResumeFileCleanupEventListener} only after the
 * enclosing transaction commits, so the DB stays the source of truth:
 * a Cloudinary failure here never rolls back a DB change, and a DB
 * rollback never triggers a delete for a file that's still referenced.
 */
public record ResumeFileCleanupEvent(String publicId) {
}
