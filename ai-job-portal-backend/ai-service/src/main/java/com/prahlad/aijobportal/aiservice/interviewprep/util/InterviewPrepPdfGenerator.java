package com.prahlad.aijobportal.aiservice.interviewprep.util;

import com.prahlad.aijobportal.aiservice.exception.AiGenerationException;
import com.prahlad.aijobportal.aiservice.interviewprep.dto.response.InterviewPrepQuestionSetResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders a generated {@link InterviewPrepQuestionSetResponse} as a
 * simple, professional PDF (title, generation metadata, then each
 * topic's questions), per the AI Interview Generator PRD's "Download
 * PDF" step. Built directly on Apache PDFBox - already a project
 * dependency for resume text extraction ({@code ResumeTextExtractionServiceImpl})
 * - rather than adding a new PDF-generation library.
 */
@Component
public class InterviewPrepPdfGenerator {

    private static final float MARGIN = 50f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm 'UTC'").withZone(ZoneOffset.UTC);

    public byte[] generate(InterviewPrepQuestionSetResponse questionSet, String resumeFileName) {
        try (PDDocument document = new PDDocument()) {
            PageCursor cursor = new PageCursor(document);

            cursor.addTitle("AI Interview Questions");
            cursor.addMeta("Resume: " + (resumeFileName == null || resumeFileName.isBlank() ? "N/A" : resumeFileName));
            cursor.addMeta("Difficulty: " + questionSet.difficulty());
            cursor.addMeta("Question Type: " + questionSet.questionType());
            cursor.addMeta("Question Count: " + questionSet.totalQuestions());
            cursor.addMeta("Generated: " + DATE_FORMAT.format(questionSet.generatedAt()));
            cursor.addSpacer();

            int number = 1;
            for (InterviewPrepQuestionSetResponse.TopicQuestionsResponse section : questionSet.sections()) {
                cursor.addSectionHeading(section.topic() + " (" + section.questions().size() + " Questions)");
                for (String question : section.questions()) {
                    cursor.addQuestion(number++, question);
                }
                cursor.addSpacer();
            }

            cursor.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new AiGenerationException("Failed to generate the interview questions PDF");
        }
    }

    /**
     * Thin stateful wrapper around PDFBox's low-level content-stream
     * API to handle pagination (starting a new page/stream once the Y
     * cursor runs off the bottom margin) so callers in {@link #generate}
     * never need to know about page boundaries.
     */
    private static final class PageCursor {
        private static final PDFont TITLE_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private static final PDFont HEADING_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private static final PDFont BODY_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        private final PDDocument document;
        private PDPageContentStream stream;
        private float cursorY;

        PageCursor(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            cursorY = PAGE_HEIGHT - MARGIN;
        }

        private void ensureSpace(float needed) throws IOException {
            if (cursorY - needed < MARGIN) {
                newPage();
            }
        }

        void addTitle(String text) throws IOException {
            ensureSpace(30);
            writeLine(text, TITLE_FONT, 18);
            cursorY -= 10;
        }

        void addMeta(String text) throws IOException {
            ensureSpace(16);
            writeLine(text, BODY_FONT, 10);
        }

        void addSpacer() {
            cursorY -= 12;
        }

        void addSectionHeading(String text) throws IOException {
            ensureSpace(26);
            writeLine(text, HEADING_FONT, 13);
            cursorY -= 4;
        }

        void addQuestion(int number, String question) throws IOException {
            for (String line : wrap(number + ". " + question, BODY_FONT, 11, PAGE_WIDTH - 2 * MARGIN)) {
                ensureSpace(16);
                writeLine(line, BODY_FONT, 11);
            }
            cursorY -= 6;
        }

        private void writeLine(String text, PDFont font, float fontSize) throws IOException {
            stream.beginText();
            stream.setFont(font, fontSize);
            stream.newLineAtOffset(MARGIN, cursorY);
            stream.showText(text);
            stream.endText();
            cursorY -= fontSize + 6;
        }

        private List<String> wrap(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
            List<String> lines = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            for (String word : text.split(" ")) {
                String candidate = current.isEmpty() ? word : current + " " + word;
                float width = font.getStringWidth(candidate) / 1000 * fontSize;
                if (width > maxWidth && !current.isEmpty()) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    current = new StringBuilder(candidate);
                }
            }
            if (!current.isEmpty()) {
                lines.add(current.toString());
            }
            return lines;
        }

        void close() throws IOException {
            stream.close();
        }
    }
}
