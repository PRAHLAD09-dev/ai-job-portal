package com.prahlad.aijobportal.recruiterservice.asset.util;

import com.prahlad.aijobportal.recruiterservice.asset.exception.InvalidImageFileException;
import com.prahlad.aijobportal.recruiterservice.config.ImageAssetProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

/**
 * Validates an uploaded logo/banner image against the configured size
 * and format constraints before it is forwarded to Cloudinary.
 *
 * <p>Mirrors the approach used by Candidate Service's
 * {@code ResumeFileValidator}: validation checks three independent
 * signals and requires all of them to agree - a file cannot pass by
 * getting just one right:
 * <ol>
 *   <li><b>Extension</b> - the claimed file extension is one of the
 *       configured {@code allowedFormats}.</li>
 *   <li><b>MIME type</b> - the browser-reported {@code Content-Type}
 *       matches what's expected for that extension. Spoofable on its
 *       own, but cheap to check and catches accidental/naive mismatches.</li>
 *   <li><b>Magic bytes</b> - the actual file bytes are inspected: JPEG
 *       must start with {@code FF D8 FF}, PNG with the 8-byte PNG
 *       signature, and WebP must have a RIFF container (bytes 0-3) with
 *       a WEBP fourCC (bytes 8-11). This is the authoritative check -
 *       renaming an unrelated file to "logo.png" no longer passes
 *       validation just because the extension and a forged Content-Type
 *       look right.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
public class ImageFileValidator {

    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] RIFF_MAGIC = {0x52, 0x49, 0x46, 0x46}; // "RIFF"
    private static final byte[] WEBP_FOURCC = {0x57, 0x45, 0x42, 0x50}; // "WEBP"

    private static final Set<String> JPEG_MIME_TYPES = Set.of("image/jpeg", "image/jpg");
    private static final Set<String> PNG_MIME_TYPES = Set.of("image/png");
    private static final Set<String> WEBP_MIME_TYPES = Set.of("image/webp");

    private final ImageAssetProperties imageAssetProperties;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageFileException("Image file must not be empty");
        }

        if (file.getSize() > imageAssetProperties.getMaxFileSizeBytes()) {
            throw new InvalidImageFileException(
                    "Image file exceeds the maximum allowed size of "
                            + (imageAssetProperties.getMaxFileSizeBytes() / (1024 * 1024)) + " MB");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(extension)) {
            throw new InvalidImageFileException(
                    "Unsupported image file format. Allowed formats: " + imageAssetProperties.getAllowedFormats());
        }
        extension = extension.toLowerCase();

        if (!imageAssetProperties.getAllowedFormats().contains(extension)) {
            throw new InvalidImageFileException(
                    "Unsupported image file format. Allowed formats: " + imageAssetProperties.getAllowedFormats());
        }

        if (!mimeTypeMatchesExtension(file.getContentType(), extension)) {
            throw new InvalidImageFileException(
                    "The uploaded file's content type does not match its '." + extension + "' extension");
        }

        if (!contentMatchesExtension(file, extension)) {
            throw new InvalidImageFileException(
                    "The uploaded file's content does not match a valid ." + extension + " image. "
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
            case "jpg", "jpeg" -> JPEG_MIME_TYPES.contains(normalized);
            case "png" -> PNG_MIME_TYPES.contains(normalized);
            case "webp" -> WEBP_MIME_TYPES.contains(normalized);
            default -> false;
        };
    }

    private boolean contentMatchesExtension(MultipartFile file, String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> startsWithMagic(file, JPEG_MAGIC);
            case "png" -> startsWithMagic(file, PNG_MAGIC);
            case "webp" -> isValidWebp(file);
            default -> false;
        };
    }

    private boolean startsWithMagic(MultipartFile file, byte[] magic) {
        byte[] header = readHeader(file, magic.length);
        return header.length == magic.length && Arrays.equals(header, magic);
    }

    /**
     * WebP files are a RIFF container: bytes 0-3 are the ASCII "RIFF"
     * signature, bytes 4-7 are a little-endian file-size field (skipped
     * here), and bytes 8-11 are the fourCC "WEBP" identifying the RIFF
     * payload type. Both parts must be present - a generic RIFF file
     * that isn't actually WebP (e.g. a renamed .wav/.avi, which are also
     * RIFF containers) is rejected.
     */
    private boolean isValidWebp(MultipartFile file) {
        byte[] header = readHeader(file, 12);
        if (header.length != 12) {
            return false;
        }
        byte[] riffPart = Arrays.copyOfRange(header, 0, 4);
        byte[] fourCcPart = Arrays.copyOfRange(header, 8, 12);
        return Arrays.equals(riffPart, RIFF_MAGIC) && Arrays.equals(fourCcPart, WEBP_FOURCC);
    }

    private byte[] readHeader(MultipartFile file, int length) {
        try (InputStream inputStream = file.getInputStream()) {
            return inputStream.readNBytes(length);
        } catch (IOException ex) {
            throw new InvalidImageFileException("Unable to read the uploaded file");
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
