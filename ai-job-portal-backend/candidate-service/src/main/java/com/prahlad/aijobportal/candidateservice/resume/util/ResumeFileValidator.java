package com.prahlad.aijobportal.candidateservice.resume.util;

import com.prahlad.aijobportal.candidateservice.resume.config.ResumeProperties;
import com.prahlad.aijobportal.candidateservice.resume.exception.InvalidResumeFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Validates an uploaded resume file against the configured size and
 * format constraints before it is forwarded to Cloudinary.
 *
 * <p>Validation checks three independent signals and requires all of
 * them to agree - a file cannot pass by getting just one right:
 * <ol>
 *   <li><b>Extension</b> - the claimed file extension is one of the
 *       configured {@code allowedFormats}.</li>
 *   <li><b>MIME type</b> - the browser-reported {@code Content-Type}
 *       matches what's expected for that extension. Spoofable on its
 *       own (this is exactly why it's not sufficient by itself), but
 *       cheap to check and catches accidental/naive mismatches.</li>
 *   <li><b>Magic bytes / content structure</b> - the actual file bytes
 *       are inspected: PDF must start with the {@code %PDF-} signature,
 *       legacy {@code .doc} must start with the OLE2 compound-file
 *       signature, and {@code .docx} must be a valid ZIP archive that
 *       actually contains a {@code word/document.xml} entry (a bare ZIP,
 *       or a ZIP that's really an .xlsx/.pptx, is rejected). This is the
 *       authoritative check - renaming an .exe or .html file to
 *       "resume.pdf" no longer passes validation just because the
 *       extension and a forged Content-Type look right.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class ResumeFileValidator {

    private static final byte[] PDF_MAGIC = {0x25, 0x50, 0x44, 0x46}; // "%PDF"
    private static final byte[] DOC_MAGIC = {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0,
            (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1}; // OLE2 compound file signature
    private static final byte[] ZIP_MAGIC = {0x50, 0x4B, 0x03, 0x04}; // "PK\3\4"

    private static final Set<String> PDF_MIME_TYPES = Set.of("application/pdf");
    private static final Set<String> DOC_MIME_TYPES = Set.of("application/msword");
    private static final Set<String> DOCX_MIME_TYPES = Set.of(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            // Some browsers/OSes report generic zip/octet-stream for docx;
            // accepted here ONLY because the magic-byte + zip-entry check
            // below is the authoritative signal for docx, not this one.
            "application/zip", "application/octet-stream");

    private final ResumeProperties resumeProperties;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidResumeFileException("Resume file must not be empty");
        }

        if (file.getSize() > resumeProperties.getMaxFileSizeBytes()) {
            throw new InvalidResumeFileException(
                    "Resume file exceeds the maximum allowed size of "
                            + (resumeProperties.getMaxFileSizeBytes() / (1024 * 1024)) + " MB");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(extension)) {
            throw new InvalidResumeFileException(
                    "Unsupported resume file format. Allowed formats: " + resumeProperties.getAllowedFormats());
        }
        extension = extension.toLowerCase();

        if (!resumeProperties.getAllowedFormats().contains(extension)) {
            throw new InvalidResumeFileException(
                    "Unsupported resume file format. Allowed formats: " + resumeProperties.getAllowedFormats());
        }

        if (!mimeTypeMatchesExtension(file.getContentType(), extension)) {
            throw new InvalidResumeFileException(
                    "The uploaded file's content type does not match its '." + extension + "' extension");
        }

        if (!contentMatchesExtension(file, extension)) {
            throw new InvalidResumeFileException(
                    "The uploaded file's content does not match a valid ." + extension + " document. "
                            + "The file may be corrupted or mislabeled.");
        }
    }

    private boolean mimeTypeMatchesExtension(String contentType, String extension) {
        if (!StringUtils.hasText(contentType)) {
            // Some HTTP clients omit Content-Type on multipart parts. The
            // magic-byte check below is authoritative regardless, so an
            // absent (not merely wrong) MIME type isn't treated as a
            // mismatch on its own.
            return true;
        }
        String normalized = contentType.toLowerCase();
        return switch (extension) {
            case "pdf" -> PDF_MIME_TYPES.contains(normalized);
            case "doc" -> DOC_MIME_TYPES.contains(normalized);
            case "docx" -> DOCX_MIME_TYPES.contains(normalized);
            default -> false;
        };
    }

    private boolean contentMatchesExtension(MultipartFile file, String extension) {
        return switch (extension) {
            case "pdf" -> startsWithMagic(file, PDF_MAGIC);
            case "doc" -> startsWithMagic(file, DOC_MAGIC);
            case "docx" -> isValidDocx(file);
            default -> false;
        };
    }

    private boolean startsWithMagic(MultipartFile file, byte[] magic) {
        byte[] header = readHeader(file, magic.length);
        return header.length == magic.length && Arrays.equals(header, magic);
    }

    /**
     * A .docx is a ZIP archive, so a correct magic-byte check alone would
     * also accept an .xlsx, .pptx, or an arbitrary renamed .zip. To be
     * specific to Word documents, this also opens the archive and
     * confirms a {@code word/document.xml} entry is present - the one
     * part every valid .docx must contain.
     */
    private boolean isValidDocx(MultipartFile file) {
        if (!startsWithMagic(file, ZIP_MAGIC)) {
            return false;
        }
        try (InputStream inputStream = file.getInputStream();
                ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName())) {
                    return true;
                }
            }
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    private byte[] readHeader(MultipartFile file, int length) {
        try (InputStream inputStream = file.getInputStream()) {
            return inputStream.readNBytes(length);
        } catch (IOException ex) {
            throw new InvalidResumeFileException("Unable to read the uploaded file");
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
