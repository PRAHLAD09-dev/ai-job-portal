package com.prahlad.aijobportal.candidateservice.resume.event;

import com.prahlad.aijobportal.candidateservice.resume.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeFileCleanupEventListener {

    private final FileStorageService fileStorageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onResumeFileCleanup(ResumeFileCleanupEvent event) {
        try {
            fileStorageService.delete(event.publicId());
        } catch (Exception ex) {
            // The DB is already committed and correct at this point - this
            // asset is genuinely orphaned storage, not a data-consistency
            // problem. Log loudly for a background reconciliation job /
            // manual cleanup rather than propagating (there's no request
            // left to fail; the caller's HTTP response was already sent).
            log.error("Failed to delete orphaned Cloudinary asset publicId={}; "
                    + "manual cleanup may be required", event.publicId(), ex);
        }
    }
}
