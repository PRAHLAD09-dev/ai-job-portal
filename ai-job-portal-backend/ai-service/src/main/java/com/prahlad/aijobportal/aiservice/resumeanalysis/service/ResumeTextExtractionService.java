package com.prahlad.aijobportal.aiservice.resumeanalysis.service;

/**
 * Turns a candidate's uploaded resume PDF into normalized plain text,
 * per DAY10_AI_Enhancement_ATS_Intelligence.md's "Resume Extraction
 * Improvements" section:
 * <pre>
 * PDF Resume -&gt; Backend -&gt; PDF Text Extraction -&gt; Gemini Analysis -&gt; Structured Response
 * </pre>
 * The candidate never supplies resume text directly - only the
 * Cloudinary URL of the PDF they uploaded via Candidate Service. This
 * is the only place in ai-service that touches the raw file.
 */
public interface ResumeTextExtractionService {

    /**
     * Downloads the PDF at {@code resumeUrl} and returns its
     * normalized text content, ready to hash and send to Gemini.
     *
     * @throws com.prahlad.aijobportal.aiservice.exception.ResumeExtractionException
     *         if the file can't be downloaded, isn't a valid/readable
     *         PDF, exceeds the configured size limit, or has no
     *         extractable text (e.g. a scanned image with no text layer)
     */
    String extractText(String resumeUrl);
}
