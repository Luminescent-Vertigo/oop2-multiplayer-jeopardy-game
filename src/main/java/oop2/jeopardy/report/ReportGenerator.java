package oop2.jeopardy.report;

import oop2.jeopardy.game.GameHistory;

/**
 * Interface for generating game summary reports for a Jeopardy game.
 * <p>
 * Implementations of this interface can generate reports in different formats,
 * such as plain text, PDF, or DOCX. Each implementation is responsible for
 * formatting and writing the report to a specified file.
 * <p>
 * The report typically includes:
 * <ul>
 *     <li>Header with title and timestamp</li>
 *     <li>Game overview (total turns, correct/incorrect counts)</li>
 *     <li>Player summaries (scores, accuracy, etc.)</li>
 *     <li>Turn-by-turn history</li>
 * </ul>
 * 
 * Implementations must handle exceptions appropriately for I/O and data errors.
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public interface ReportGenerator {

    /**
     * Generates a game report based on the provided {@link GameHistory} and writes
     * it to the specified file.
     * <p>
     * Implementations may append a file extension if necessary and are responsible
     * for all formatting, including headers, tables, and per-turn information.
     *
     * @param history  the {@link GameHistory} containing all game records and statistics
     * @param filename the path of the output file
     * @throws Exception if an error occurs during report generation or file writing
     */
    public void generate(GameHistory history, String filename) throws Exception;
}
