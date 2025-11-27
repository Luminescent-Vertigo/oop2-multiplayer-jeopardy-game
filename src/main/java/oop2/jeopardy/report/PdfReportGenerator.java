package oop2.jeopardy.report;

import oop2.jeopardy.game.GameHistory;
import oop2.jeopardy.game.GameRecord;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates a detailed PDF report summarizing a Jeopardy game.
 * <p>
 * The report includes:
 * <ul>
 *     <li>Main title bar with timestamp</li>
 *     <li>Game overview (total turns, correct/incorrect answers)</li>
 *     <li>Player summary (final score, correct/incorrect counts, accuracy)</li>
 *     <li>Final scores table</li>
 *     <li>Statistics: most correct and most incorrect answers</li>
 *     <li>Turn-by-turn history with detailed question information</li>
 * </ul>
 * <p>
 * Uses Apache PDFBox for PDF creation and rendering, with support for multiple pages.
 * Font: Noto Sans (embedded) is required for proper rendering.
 * <p>
 * Page management automatically adds new pages when content reaches the bottom margin.
 * All tables and sections are formatted for readability with color coding, alignment, and spacing.
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */

/**
 * Generates reports in PDF format.
 */
public class PdfReportGenerator implements ReportGenerator {

    /** Page margin in points */
    private static final float MARGIN = 50f;

    /** Line spacing in points */
    private static final float LINE_SPACING = 16f;

    /** Title font size in points */
    private static final float TITLE_FONT_SIZE = 18f;

    /** Dark gold color for title bar */
    private static final Color GOLD = new Color(0xC8, 0xA2, 0x00);

    /** Pale gold color for section headers and table headers */
    private static final Color PALE_GOLD = new Color(0xFF, 0xF4, 0xB8);


    /**
     * Inner class that holds and to keep track of the current page, content stream, and y-position.
     */
    private static class PageContext {

        /** The PDF page currently being written to. */
        PDPage page;

        /** The content stream used to draw on the PDF page. */
        PDPageContentStream cs;

        /** Current vertical position on the page for writing text. */
        float y;

        /** Constructs a new {@code PageContext} with the given page, content stream, and y-position. 
         * @param page  the given PDF page
         * @param cs    the content stream for drawing on the page
         * @param y     the current vertical position on the page
         */
        PageContext(PDPage page, PDPageContentStream cs, float y) {
            this.page = page;
            this.cs = cs;
            this.y = y;
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    public PdfReportGenerator() {
        // Prevent direct instantiation
    }


    /**
     * Generates a PDF report summarizing the provided {@link GameHistory}.
     *
     * @param history  the game history to summarize
     * @param filename the output file path (or resource name)
     * @throws Exception if an I/O or PDF generation error occurs
     */
    @Override
    public void generate(GameHistory history, String filename) throws Exception {
        if (!filename.endsWith(".pdf")) filename += ".pdf";

        PDDocument doc = new PDDocument();
        PDPage first = new PDPage(PDRectangle.LETTER);
        doc.addPage(first);

        // Load Noto Sans
        InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/NotoSans-Regular.ttf");
        if (fontStream == null) {
            throw new RuntimeException("Missing font resource: src/main/resources/fonts/NotoSans-Regular.ttf");
        }
        PDType0Font font = PDType0Font.load(doc, fontStream);

        PageContext ctx = new PageContext(
                first,
                new PDPageContentStream(doc, first),
                first.getMediaBox().getHeight() - MARGIN
        );

        try {
            /* =============================
             * TITLE BAR
             * ============================= */
            ctx.cs.setNonStrokingColor(GOLD);
            ctx.cs.addRect(0, ctx.y - 36, ctx.page.getMediaBox().getWidth(), 36);
            ctx.cs.fill();
            ctx.cs.setNonStrokingColor(Color.BLACK);

            ctx.cs.beginText();
            ctx.cs.setFont(font, TITLE_FONT_SIZE);
            ctx.cs.setNonStrokingColor(Color.WHITE);
            float titleWidth = font.getStringWidth("JEOPARDY GAME SUMMARY REPORT") / 1000 * TITLE_FONT_SIZE;
            ctx.cs.newLineAtOffset((ctx.page.getMediaBox().getWidth() - titleWidth) / 2f, ctx.y - 26);
            ctx.cs.showText("JEOPARDY GAME SUMMARY REPORT");
            ctx.cs.endText();
            ctx.cs.setNonStrokingColor(Color.BLACK);

            ctx.y -= 48;
            ctx.y = line(ctx, font, " ");

            /* =============================
             * TIMESTAMP
             * ============================= */
            ctx.cs.beginText();
            ctx.cs.setFont(font, 10);
            ctx.cs.newLineAtOffset(MARGIN, ctx.y);
            ctx.cs.showText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ctx.cs.endText();
            ctx.y -= 22;

            /* =============================
             * GAME OVERVIEW
             * ============================= */
            ctx.y = sectionHeader(ctx, font, "GAME OVERVIEW");

            int totalTurns = history.size();
            int totalCorrect = history.getCorrectCounts().values().stream().mapToInt(i -> i).sum();
            int totalIncorrect = history.getIncorrectCounts().values().stream().mapToInt(i -> i).sum();

            ctx.y = line(ctx, font, " ");
            ctx.y = infoLine(ctx, font, "Players", "" + history.getFinalScores().size());
            ctx.y = infoLine(ctx, font, "Total Turns", "" + totalTurns);
            ctx.y = infoLine(ctx, font, "Total Correct", "" + totalCorrect);
            ctx.y = infoLine(ctx, font, "Total Incorrect", "" + totalIncorrect);
            ctx.y = line(ctx, font, " ");

            /* =============================
             * PLAYER SUMMARY
             * ============================= */
            ctx.y = sectionHeader(ctx, font, "PLAYER SUMMARY");

            Map<String, Integer> finalScores = history.getFinalScores();
            Map<String, Integer> correct = history.getCorrectCounts();
            Map<String, Integer> incorrect = history.getIncorrectCounts();
            Map<String, Double> accuracy = history.getAccuracyMap();

            for (String player : finalScores.keySet()) {
                ctx = ensureSpace(doc, ctx, font);
                ctx.y = line(ctx, font, " ");
                //ctx.y = line(ctx, font, "------------------------------------------------------------");
                ctx.y = line(ctx, font, "============================================================");
                ctx.y = labeledLine(ctx, font, "Player Name", player);
                ctx.y = line(ctx, font, "------------------------------------------------------------");
                ctx.y = labeledLine(ctx, font, "Final Score", "" + finalScores.get(player));
                ctx.y = labeledLine(ctx, font, "Correct Answers", "" + correct.getOrDefault(player, 0));
                ctx.y = labeledLine(ctx, font, "Incorrect Answers", "" + incorrect.getOrDefault(player, 0));
                ctx.y = labeledLine(ctx, font, "Accuracy", String.format("%.2f%%", accuracy.getOrDefault(player, 0.0)));
                //ctx.y = line(ctx, font, "------------------------------------------------------------");
                ctx.y = line(ctx, font, "============================================================");
                ctx.y -= 8;
            }

            /* =============================
             * FINAL SCORES TABLE
             * ============================= */
            ctx.y = sectionHeader(ctx, font, "FINAL SCORES");

            List<Map.Entry<String, Integer>> ranking = finalScores.entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .collect(Collectors.toList());

            ctx.y = tableHeader(ctx, font, PALE_GOLD, "Rank", "Player", "Score");

            int rank = 1;
            for (var entry : ranking) {
                ctx = ensureSpace(doc, ctx, font);
                ctx.y = tableRow(ctx, font, "" + rank++, entry.getKey(), "" + entry.getValue());
            }

            ctx.y = line(ctx, font, " ");

            /* =============================
             * STATISTICS
             * ============================= */
            ctx.y = sectionHeader(ctx, font, "STATISTICS");
            ctx.y = line(ctx, font, " ");

            var best = history.getMostCorrect();
            var worst = history.getMostIncorrect();

            if (best != null)
                ctx.y = labeledLine(ctx, font, "Most Correct Answers", best.getKey() + " (" + best.getValue() + ")");
            if (worst != null)
                ctx.y = labeledLine(ctx, font, "Most Incorrect Answers", worst.getKey() + " (" + worst.getValue() + ")");

            ctx.y -= 6;
            ctx.y = tableHeader(ctx, font, PALE_GOLD, "Player", "Correct", "Incorrect", "Accuracy");

            for (String p : finalScores.keySet()) {
                ctx = ensureSpace(doc, ctx, font);
                ctx.y = tableRow(ctx, font,
                        p,
                        "" + correct.getOrDefault(p, 0),
                        "" + incorrect.getOrDefault(p, 0),
                        String.format("%.2f%%", accuracy.getOrDefault(p, 0.0))
                );
            }

            ctx.y = line(ctx, font, " ");

            /* =============================
             * TURN-BY-TURN HISTORY
             * ============================= */
            ctx.y = sectionHeader(ctx, font, "TURN-BY-TURN HISTORY");
            ctx.y = line(ctx, font, " ");


            int turn = 1;
            for (GameRecord r : history.getRecords()) {
                ctx = ensureSpace(doc, ctx, font);
                ctx.y = line(ctx, font, " ");
                ctx.y = line(ctx, font, "============================================================");
                //ctx.y = line(ctx, font, "------------------------------------------------------------");
                ctx.y = labeledLine(ctx, font, "TURN", "" + turn++);
                ctx.y = line(ctx, font, "------------------------------------------------------------");
                ctx.y = labeledLine(ctx, font, "Player", r.getPlayerName());
                ctx.y = labeledLine(ctx, font, "Category", r.getCategory());
                ctx.y = labeledLine(ctx, font, "Value", "" + r.getValue());
                ctx.y = labeledLine(ctx, font, "Question", r.getQuestionText());
                ctx.y = labeledLine(ctx, font, "Player Answer", r.getUserAnswer());
                ctx.y = labeledLine(ctx, font, "Correct Answer", r.getCorrectAnswer());
                ctx.y = labeledLine(ctx, font, "Result", r.isCorrect() ? "CORRECT" : "INCORRECT");
                ctx.y = labeledLine(ctx, font, "Points Earned", "" + r.getPointsEarned());
                ctx.y = labeledLine(ctx, font, "Running Score", "" + r.getRunningScore());
                //ctx.y = line(ctx, font, "------------------------------------------------------------");
                ctx.y = line(ctx, font, "============================================================");
                ctx.y -= 10;
            }

        } finally {
            ctx.cs.close();
        }

        doc.save(new File(filename));
        doc.close();
    }


    /* ============================================================
     * Page management (FIXED)
     * ============================================================ */

    /**
     * Ensures there is sufficient space on the current page, otherwise adds a new page.
     *
     * @param doc  the PDF document
     * @param ctx  the current page context
     * @param font the font used for text measurement
     * @return updated page context
     * @throws Exception if creating a new page/content stream fails
     */
    private PageContext ensureSpace(PDDocument doc, PageContext ctx, PDType0Font font) throws Exception {
        if (ctx.y < 80) {
            ctx.cs.close();

            PDPage newPage = new PDPage(PDRectangle.LETTER);
            doc.addPage(newPage);

            PDPageContentStream newCs = new PDPageContentStream(doc, newPage);

            return new PageContext(
                    newPage,
                    newCs,
                    newPage.getMediaBox().getHeight() - MARGIN
            );
        }
        return ctx;
    }


    /* ============================================================
     * Drawing helpers
     * ============================================================ */

    /**
     * Draws a section header bar with pale-gold background and title text.
     *
     * @param ctx   the page context
     * @param font  the font to use
     * @param title the section title
     * @return updated y-position after drawing
     * @throws Exception if drawing fails
     */
    private float sectionHeader(PageContext ctx, PDType0Font font, String title) throws Exception {
        float barHeight = 18f;
        float width = ctx.page.getMediaBox().getWidth() - (2 * MARGIN);

        ctx.cs.setNonStrokingColor(PALE_GOLD);
        ctx.cs.addRect(MARGIN, ctx.y - barHeight, width, barHeight);
        ctx.cs.fill();
        ctx.cs.setNonStrokingColor(Color.BLACK);

        ctx.cs.beginText();
        ctx.cs.setFont(font, 12);
        ctx.cs.newLineAtOffset(MARGIN + 4, ctx.y - 14);
        ctx.cs.showText(title);
        ctx.cs.endText();

        return ctx.y - (barHeight + 8);
    }


    /**
     * Writes a line with label and value (info line).
     *
     * @param ctx   the page context
     * @param font  the font to use
     * @param label the label text
     * @param value the value text
     * @return updated y-position after drawing
     * @throws Exception if drawing fails
     */
    private float infoLine(PageContext ctx, PDType0Font font, String label, String value) throws Exception {
        ctx.cs.beginText();
        ctx.cs.setFont(font, 11);
        ctx.cs.newLineAtOffset(MARGIN, ctx.y);
        ctx.cs.showText(label + ": " + value);
        ctx.cs.endText();
        return ctx.y - LINE_SPACING;
    }


    /**
     * Writes a line with label and value for general use.
     *
     * @param ctx   the page context
     * @param font  the font to use
     * @param label the label text
     * @param value the value text
     * @return updated y-position after drawing
     * @throws Exception if drawing fails
     */
    private float labeledLine(PageContext ctx, PDType0Font font, String label, String value) throws Exception {
        ctx.cs.beginText();
        ctx.cs.setFont(font, 11);
        ctx.cs.newLineAtOffset(MARGIN, ctx.y);
        ctx.cs.showText(label + ": " + value);
        ctx.cs.endText();
        return ctx.y - LINE_SPACING;
    }


    /**
     * Writes a generic line of text at the current y-position.
     *
     * @param ctx  the page context
     * @param font the font to use
     * @param text the text to write
     * @return updated y-position after drawing
     * @throws Exception if drawing fails
     */
    private float line(PageContext ctx, PDType0Font font, String text) throws Exception {
        ctx.cs.beginText();
        ctx.cs.setFont(font, 11);
        ctx.cs.newLineAtOffset(MARGIN, ctx.y);
        ctx.cs.showText(text);
        ctx.cs.endText();
        return ctx.y - LINE_SPACING;
    }


    /**
     * Draws a table header with colored background and column titles.
     *
     * @param ctx     the page context
     * @param font    the font to use
     * @param fill    background color
     * @param headers column header texts
     * @return updated y-position after drawing
     * @throws Exception if drawing fails
     */
    private float tableHeader(PageContext ctx, PDType0Font font, Color fill, String... headers) throws Exception {
        float tableWidth = ctx.page.getMediaBox().getWidth() - (2 * MARGIN);
        int cols = headers.length;
        float colWidth = tableWidth / cols;
        float headerHeight = 18f;

        ctx.cs.setNonStrokingColor(fill);
        ctx.cs.addRect(MARGIN, ctx.y - headerHeight, tableWidth, headerHeight);
        ctx.cs.fill();
        ctx.cs.setNonStrokingColor(Color.BLACK);

        float x = MARGIN;
        for (String h : headers) {
            ctx.cs.beginText();
            ctx.cs.setFont(font, 11);
            ctx.cs.newLineAtOffset(x + 4, ctx.y - 12);
            ctx.cs.showText(h);
            ctx.cs.endText();
            x += colWidth;
        }

        return ctx.y - (headerHeight + 6);
    }


    /**
     * Draws a table row with provided cell values.
     *
     * @param ctx   the page context
     * @param font  the font to use
     * @param cells the cell texts
     * @return updated y-position after drawing
     * @throws Exception if drawing fails
     */
    private float tableRow(PageContext ctx, PDType0Font font, String... cells) throws Exception {
        float tableWidth = ctx.page.getMediaBox().getWidth() - (2 * MARGIN);
        int cols = cells.length;
        float colWidth = tableWidth / cols;

        float x = MARGIN;
        for (String text : cells) {
            ctx.cs.beginText();
            ctx.cs.setFont(font, 11);
            ctx.cs.newLineAtOffset(x + 4, ctx.y - 12);
            ctx.cs.showText(text);
            ctx.cs.endText();
            x += colWidth;
        }

        return ctx.y - LINE_SPACING;
    }
}
