package oop2.jeopardy.report;

import oop2.jeopardy.game.GameHistory;
import oop2.jeopardy.game.GameRecord;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates a detailed DOCX report summarizing a Jeopardy game.
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
 * Uses Apache POI XWPFDocument for DOCX creation and formatting.
 * All tables and sections are formatted for readability with color coding, alignment, and spacing.
 * <p>
 * Note: The filename passed to {@link #generate(GameHistory, String)} will automatically
 * append ".docx" if not already present.
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */

/**
 * Generates reports in DOCX format.
 */
public class DocxReportGenerator implements ReportGenerator {

    /** Dark gold color used for main title bar. */
    private static final String GOLD = "C8A200";       

    /** Pale gold color used for section headers and table headers. */
    private static final String PALE_GOLD = "FFF4B8"; 

    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    public DocxReportGenerator() {
        // Prevent direct instantiation
    }

    /**
     * Generates a DOCX report summarizing the provided {@link GameHistory}.
     *
     * @param history  the game history to summarize
     * @param filename the output file path (or resource name)
     * @throws Exception if an I/O or document generation error occurs
     */
    @Override
    public void generate(GameHistory history, String filename) throws Exception {
        if (!filename.endsWith(".docx")) filename += ".docx";

        XWPFDocument doc = new XWPFDocument();

        try {
            /* =============================
             * MAIN TITLE BAR
             * ============================= */
            XWPFTable titleTbl = createFullWidthBar(doc, GOLD);
            XWPFParagraph titlePara = titleTbl.getRow(0).getCell(0).getParagraphs().get(0);
            titlePara.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun titleRun = titlePara.createRun();
            titleRun.setBold(true);
            titleRun.setColor("FFFFFF");
            titleRun.setFontSize(20);
            titleRun.setText("JEOPARDY GAME SUMMARY REPORT");
            titleRun.addBreak();

            XWPFRun ts = titlePara.createRun();
            ts.setFontSize(10);
            ts.setItalic(true);
            ts.setColor("FFFFFF");
            ts.setText("Generated: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            //ts.addBreak();

            doc.createParagraph().createRun().addBreak();

            /* =============================
             * GAME OVERVIEW
             * ============================= */
            createSectionHeader(doc, "GAME OVERVIEW");

            int totalTurns = history.size();
            int totalCorrect = history.getCorrectCounts().values().stream().mapToInt(i -> i).sum();
            int totalIncorrect = history.getIncorrectCounts().values().stream().mapToInt(i -> i).sum();

            addKeyValue(doc, "Players", String.valueOf(history.getFinalScores().size()));
            addKeyValue(doc, "Total Turns", String.valueOf(totalTurns));
            addKeyValue(doc, "Total Correct", String.valueOf(totalCorrect));
            addKeyValue(doc, "Total Incorrect", String.valueOf(totalIncorrect));
            
            doc.createParagraph().createRun().addBreak();


            /* =============================
             * PLAYER SUMMARY
             * ============================= */
            createSectionHeader(doc, "PLAYER SUMMARY");

            Map<String, Integer> finalScores = history.getFinalScores();
            Map<String, Integer> correct = history.getCorrectCounts();
            Map<String, Integer> incorrect = history.getIncorrectCounts();
            Map<String, Double> accuracy = history.getAccuracyMap();

            for (String player : finalScores.keySet()) {

                XWPFParagraph sep = doc.createParagraph();
                XWPFRun sepRun = sep.createRun();
                sepRun.setText("------------------------------------------------------------");
                //sepRun.addBreak();

                addLabeledLine(doc, "Player Name", player);
                addLabeledLine(doc, "Final Score", String.valueOf(finalScores.get(player)));
                addLabeledLine(doc, "Correct Answers", String.valueOf(correct.getOrDefault(player, 0)));
                addLabeledLine(doc, "Incorrect Answers", String.valueOf(incorrect.getOrDefault(player, 0)));
                addLabeledLine(doc, "Accuracy", String.format("%.2f%%", accuracy.getOrDefault(player, 0.0)));
                addSeparatorLine(doc);


                doc.createParagraph().createRun().addBreak();
            }

            /* =============================
             * FINAL SCORES TABLE
             * ============================= */
            createSectionHeader(doc, "FINAL SCORES");

            List<Map.Entry<String, Integer>> ranking = finalScores.entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .collect(Collectors.toList());

            XWPFTable scoreTable = createAutoWidthTable(doc, 3);
            setTableHeader(scoreTable.getRow(0), "Rank", "Player", "Score");

            int rank = 1;
            for (var entry : ranking) {
                XWPFTableRow row = scoreTable.createRow();
                row.getCell(0).setText(String.valueOf(rank++));
                centerCell(row.getCell(0));

                row.getCell(1).setText(entry.getKey());
                centerCell(row.getCell(1));

                row.getCell(2).setText(String.valueOf(entry.getValue()));
                centerCell(row.getCell(2));
            }

            doc.createParagraph().createRun().addBreak();


            /* =============================
             * STATISTICS
             * ============================= */
            createSectionHeader(doc, "STATISTICS");

            var mostCorrect = history.getMostCorrect();
            var mostIncorrect = history.getMostIncorrect();

            
            if (mostCorrect != null) {
                addSeparatorLine(doc);
                addLabeledLine(doc, "Most Correct Answers",
                        mostCorrect.getKey() + " (" + mostCorrect.getValue() + ")");
                addSeparatorLine(doc);

            }
            if (mostIncorrect != null) {
                addSeparatorLine(doc);
                addLabeledLine(doc, "Most Incorrect Answers",
                        mostIncorrect.getKey() + " (" + mostIncorrect.getValue() + ")");
                addSeparatorLine(doc);

            }

            doc.createParagraph().createRun().addBreak();


            /* Accuracy Table */
            XWPFTable accTable = createAutoWidthTable(doc, 4);
            setTableHeader(accTable.getRow(0), "Player", "Correct", "Incorrect", "Accuracy");

            for (String p : finalScores.keySet()) {
                XWPFTableRow row = accTable.createRow();
                row.getCell(0).setText(p);
                centerCell(row.getCell(0));

                row.getCell(1).setText(String.valueOf(correct.getOrDefault(p, 0)));
                centerCell(row.getCell(1));

                row.getCell(2).setText(String.valueOf(incorrect.getOrDefault(p, 0)));
                centerCell(row.getCell(2));

                row.getCell(3).setText(String.format("%.2f%%", accuracy.getOrDefault(p, 0.0)));
                centerCell(row.getCell(3));
            }

            doc.createParagraph().createRun().addBreak();


            /* =============================
             * TURN-BY-TURN HISTORY
             * ============================= */
            createSectionHeader(doc, "TURN-BY-TURN HISTORY");

            int turn = 1;
            for (GameRecord rcd : history.getRecords()) {

                XWPFParagraph titleP = doc.createParagraph();
                XWPFRun tr = titleP.createRun();
                tr.setBold(true);
                tr.setText("------------------------------------------------------------");
                tr.addBreak();
                tr.setText("TURN " + turn++);
                tr.addBreak();
                tr.setText("------------------------------------------------------------");
                tr.addBreak();

                addLabeledLine(doc, "Player", rcd.getPlayerName());
                addLabeledLine(doc, "Category", rcd.getCategory());
                addLabeledLine(doc, "Value", String.valueOf(rcd.getValue()));
                addLabeledLine(doc, "Question", rcd.getQuestionText());
                addLabeledLine(doc, "Player Answer", rcd.getUserAnswer());
                addLabeledLine(doc, "Correct Answer", rcd.getCorrectAnswer());
                addLabeledLine(doc, "Result", rcd.isCorrect() ? "CORRECT" : "INCORRECT");
                addLabeledLine(doc, "Points Earned", String.valueOf(rcd.getPointsEarned()));
                addLabeledLine(doc, "Running Score", String.valueOf(rcd.getRunningScore()));
                addSeparatorLine(doc);

                doc.createParagraph().createRun().addBreak();
            }

            // Write file
            try (FileOutputStream out = new FileOutputStream(filename)) {
                doc.write(out);
            }

        } finally {
            doc.close();
        }
    }


    /* =====================================================
     *  HELPERS
     * ===================================================== */

    /**
     * Creates a section header with a using 1×1 full-width pale-gold bar.
     *
     * @param doc   the document to add the header to
     * @param title the title text of the section
     */
    private void createSectionHeader(XWPFDocument doc, String title) {
        XWPFTable tbl = createFullWidthBar(doc, PALE_GOLD);
        XWPFParagraph p = tbl.getRow(0).getCell(0).getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setFontSize(13);
        r.setColor("000000");
        r.setText(title);
        //r.addBreak();

        doc.createParagraph().createRun().addBreak();
    }


    /**
     * Creates a full-width 1×1 table bar with a solid background color.
     *
     * @param doc   the document to add the table to
     * @param color the background color as hex string (e.g., "C8A200")
     * @return the created table
     */
    private XWPFTable createFullWidthBar(XWPFDocument doc, String color) {
        XWPFTable tbl = doc.createTable(1, 1);
        tbl.setWidth("100%");
        XWPFTableCell cell = tbl.getRow(0).getCell(0);
        cell.setColor(color);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        // add padding
        cell.getCTTc().addNewTcPr().addNewTcMar().addNewLeft().setW(200);
        return tbl;
    }


    /**
     * Creates a table with automatic full width and padded cells.
     *
     * @param doc  the document to add the table to
     * @param cols number of columns
     * @return the created table
     */
    private XWPFTable createAutoWidthTable(XWPFDocument doc, int cols) {
        XWPFTable table = doc.createTable(1, cols);
        table.setWidth("100%");

        for (int i = 0; i < cols; i++) {
            XWPFTableCell cell = table.getRow(0).getCell(i);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);         // vertical center

            // add padding inside cells
            var tcMar = cell.getCTTc().addNewTcPr().addNewTcMar();
            tcMar.addNewLeft().setW(120);
            tcMar.addNewRight().setW(120);

            // Apply horizontal center alignment
            XWPFParagraph p = cell.getParagraphs().get(0);
            p.setAlignment(ParagraphAlignment.CENTER);
        }
        return table;
    }


    /**
     * Adds a bold label and its corresponding value on a paragraph.
     *
     * @param doc   the document to add the line to
     * @param label the label text
     * @param value the value text
     */
    private void addKeyValue(XWPFDocument doc, String label, String value) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun l = p.createRun();
        l.setBold(true);
        l.setText(label + ": ");

        XWPFRun v = p.createRun();
        v.setText(value);
    }


    /**
     * Adds a labeled line for a key-value pair.
     *
     * @param doc   the document to add the line to
     * @param label the label text
     * @param value the value text
     */
    private void addLabeledLine(XWPFDocument doc, String label, String value) {
        XWPFParagraph p = doc.createParagraph();

        XWPFRun l = p.createRun();
        l.setBold(true);
        l.setText(label + ": ");

        XWPFRun v = p.createRun();
        v.setText(value);
    }


    /**
     * Sets header cells of a table row with bold text and pale-gold background.
     *
     * @param row   the table row
     * @param texts the header texts
     */
    private void setTableHeader(XWPFTableRow row, String... texts) {
        for (int i = 0; i < texts.length; i++) {

            XWPFTableCell cell =
                    (i < row.getTableCells().size())
                            ? row.getCell(i)
                            : row.addNewTableCell();

            cell.setColor(PALE_GOLD);

            XWPFParagraph p = cell.getParagraphs().get(0);
            p.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun r = p.createRun();
            r.setBold(true);
            r.setText(texts[i]);
        }
    }


    /**
     * Centers the text of a table cell horizontally.
     *
     * @param cell the cell to center
     */
    private void centerCell(XWPFTableCell cell) {
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.CENTER);
    }


    /**
     * Adds a full-width horizontal separator line (text-based) as its own paragraph.
     *
     * @param doc the document to add the separator to
     */
    private void addSeparatorLine(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText("------------------------------------------------------------");
        //r.addBreak();
    }

}
