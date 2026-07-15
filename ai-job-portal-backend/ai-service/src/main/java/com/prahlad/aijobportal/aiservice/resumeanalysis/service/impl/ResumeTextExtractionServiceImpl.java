package com.prahlad.aijobportal.aiservice.resumeanalysis.service.impl;

import com.prahlad.aijobportal.aiservice.exception.ResumeExtractionException;
import com.prahlad.aijobportal.aiservice.resumeanalysis.config.ResumeExtractionProperties;
import com.prahlad.aijobportal.aiservice.resumeanalysis.service.ResumeTextExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;

/**
 * Implements the pipeline called for in
 * DAY10_AI_Enhancement_ATS_Intelligence.md's "Resume Extraction
 * Improvements" section:
 * <pre>
 * PDF Resume -&gt; Backend -&gt; PDF Text Extraction -&gt; Structured Parsing -&gt; Gemini Analysis -&gt; Structured Response
 * </pre>
 * "Structured parsing" (Summary/Skills/Experience/Education/Projects/
 * Certifications/Languages/Achievements) is Gemini's job, not a
 * regex/rules layer here - the resume PDF's structure is far too
 * inconsistent across candidates for reliable rule-based section
 * splitting, and DECISIONS.md already commits this platform to Gemini
 * for exactly this kind of unstructured-to-structured extraction. This
 * class's job ends at "clean, normalized plain text" -
 * {@code ResumeAnalysisServiceImpl} feeds that text into the same
 * Gemini prompt (see {@code ResumeAnalysisAiResult}) that already
 * requests those structured fields.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeTextExtractionServiceImpl implements ResumeTextExtractionService {

    private static final byte[] PDF_MAGIC_BYTES = {'%', 'P', 'D', 'F'};

    private final WebClient resumeFileWebClient;
    private final ResumeExtractionProperties properties;

    @Override
    public String extractText(String resumeUrl) {
        byte[] fileBytes = download(resumeUrl);
        validatePdfSignature(fileBytes);

        String rawText = extractRawText(fileBytes);
        String normalized = normalize(rawText);

        if (normalized.length() < properties.getMinExtractedTextLength()) {
            throw new ResumeExtractionException(
                    "No readable text could be found in this resume. It may be a scanned image without a text "
                            + "layer, or an empty document - please upload a text-based PDF resume.");
        }

        if (normalized.length() > properties.getMaxExtractedTextLength()) {
            normalized = normalized.substring(0, properties.getMaxExtractedTextLength());
        }

        log.info("Extracted {} characters of resume text ({} raw bytes)", normalized.length(), fileBytes.length);
        return normalized;
    }

    private byte[] download(String resumeUrl) {
        byte[] fileBytes;
        try {
            fileBytes = resumeFileWebClient.get()
                    .uri(resumeUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block(Duration.ofSeconds(15));
        } catch (ResumeExtractionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResumeExtractionException(
                    "Unable to download the resume file. Please check that the resume was uploaded successfully and try again.", ex);
        }

        if (fileBytes == null || fileBytes.length == 0) {
            throw new ResumeExtractionException("The resume file is empty or could not be downloaded.");
        }

        if (fileBytes.length > properties.getMaxFileSizeBytes()) {
            throw new ResumeExtractionException(
                    "The resume file is too large (max %d MB). Please upload a smaller PDF."
                            .formatted(properties.getMaxFileSizeBytes() / (1024 * 1024)));
        }

        return fileBytes;
    }

    private void validatePdfSignature(byte[] fileBytes) {
        if (fileBytes.length < PDF_MAGIC_BYTES.length) {
            throw new ResumeExtractionException("The uploaded file is not a valid PDF.");
        }
        for (int i = 0; i < PDF_MAGIC_BYTES.length; i++) {
            if (fileBytes[i] != PDF_MAGIC_BYTES[i]) {
                throw new ResumeExtractionException(
                        "The uploaded file is not a valid PDF. Please upload your resume in PDF format.");
            }
        }
    }

    private String extractRawText(byte[] fileBytes) {
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            if (document.isEncrypted()) {
                throw new ResumeExtractionException(
                        "This PDF is password-protected. Please upload an unprotected PDF resume.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        } catch (InvalidPasswordException ex) {
            throw new ResumeExtractionException(
                    "This PDF is password-protected. Please upload an unprotected PDF resume.", ex);
        } catch (IOException ex) {
            throw new ResumeExtractionException(
                    "The resume PDF appears to be corrupted or unreadable. Please re-upload a valid PDF.", ex);
        }
    }

    /** Collapses inconsistent PDF whitespace/line-break extraction artifacts into clean, normalized text. */
    private String normalize(String rawText) {
        if (rawText == null) {
            return "";
        }
        return rawText
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \\t\\x0B\\f]+", " ")
                .replaceAll(" *\\n *", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .strip();
    }
}
