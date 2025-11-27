package oop2.jeopardy.report;

import oop2.jeopardy.game.GameHistory;
import oop2.jeopardy.game.GameRecord;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Generates a plain-text (.txt) summary report for a Jeopardy game.
 * <p>
 * The report includes:
 * <ul>
 *     <li>Main header with title and timestamp</li>
 *     <li>Game overview (total turns, total correct/incorrect answers)</li>
 *     <li>Player summary (final score, correct/incorrect counts, accuracy)</li>
 *     <li>Final scores table with ranks</li>
 *     <li>Statistics: most correct and most incorrect answers</li>
 *     <li>Accuracy breakdown per player</li>
 *     <li>Turn-by-turn history with detailed question information</li>
 * </ul>
 * <p>
 * Text formatting uses fixed-width alignment and simple wrapping for long text.
 * This generator is ideal for environments where a plain-text report is sufficient.
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */

/**
 * Generates reports in TXT format.
 */
public class TxtReportGenerator implements ReportGenerator {

    /** Maximum number of characters per line before wrapping text */
    private static final int WRAP_WIDTH = 72;


    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    public TxtReportGenerator() {
         // Prevent direct instantiation
    }


    /**
     * Generates a plain-text report summarizing the provided {@link GameHistory}.
     * <p>
     * The output includes a header, game overview, player summaries, final scores,
     * statistics, accuracy breakdown, and turn-by-turn history. Automatically appends
     * ".txt" extension if not present.
     *
     * @param history  the game history to summarize
     * @param filename the output file path
     * @throws Exception if an I/O error occurs while writing the file
     * @throws NullPointerException if history or filename is null
     */
    @Override
    public void generate(GameHistory history, String filename) throws Exception {

        if (history == null) {
            throw new NullPointerException("GameHistory cannot be null");
        }

        if (filename == null) {
            throw new NullPointerException("Filename cannot be null");
        }

        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }

        try (FileWriter w = new FileWriter(filename)) {

            /* ================================================================
             * HEADER
             * ================================================================ */
            w.write("============================================================\n");
            w.write("                 JEOPARDY GAME SUMMARY REPORT\n");
            w.write("============================================================\n");
            w.write("Generated: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                    "\n\n");


            /* ================================================================
             * GAME OVERVIEW
             * ================================================================ */
            int totalTurns = history.size();
            int totalCorrect = history.getCorrectCounts().values().stream().mapToInt(i -> i).sum();
            int totalIncorrect = history.getIncorrectCounts().values().stream().mapToInt(i -> i).sum();

            w.write("============================================================\n");
            w.write("                        GAME OVERVIEW\n");
            w.write("============================================================\n\n");

            w.write(String.format("%-18s : %s%n", "Players", history.getFinalScores().size()));
            w.write(String.format("%-18s : %s%n", "Total Turns", totalTurns));
            w.write(String.format("%-18s : %s%n", "Total Correct", totalCorrect));
            w.write(String.format("%-18s : %s%n%n", "Total Incorrect", totalIncorrect));


            /* ================================================================
             * PLAYER SUMMARY
             * ================================================================ */
            w.write("============================================================\n");
            w.write("                       PLAYER SUMMARY\n");
            w.write("============================================================\n\n");

            Map<String, Integer> finalScores = history.getFinalScores();
            Map<String, Integer> correctCounts = history.getCorrectCounts();
            Map<String, Integer> incorrectCounts = history.getIncorrectCounts();
            Map<String, Double> accuracyMap = history.getAccuracyMap();

            for (String player : finalScores.keySet()) {

                w.write("------------------------------------------------------------\n");
                w.write(String.format("%-18s : %s%n", "Player Name", player));
                w.write(String.format("%-18s : %d%n", "Final Score", finalScores.get(player)));
                w.write(String.format("%-18s : %d%n", "Correct Answers", correctCounts.getOrDefault(player, 0)));
                w.write(String.format("%-18s : %d%n", "Incorrect Answers", incorrectCounts.getOrDefault(player, 0)));
                w.write(String.format("%-18s : %.2f%%%n", "Accuracy", accuracyMap.getOrDefault(player, 0.0)));
                w.write("------------------------------------------------------------\n\n");
            }


            /* ================================================================
             * FINAL SCORES TABLE
             * ================================================================ */
            w.write("============================================================\n");
            w.write("                         FINAL SCORES\n");
            w.write("============================================================\n\n");

            w.write(String.format("%-5s %-18s %-10s%n", "Rank", "Player", "Score"));
            w.write("------------------------------------------------------------\n");

            int rank = 1;
            for (var entry : finalScores.entrySet()) {
                w.write(String.format("%-5s %-18s %-10d%n",
                        rank++, entry.getKey(), entry.getValue()));
            }

            w.write("\n");


            /* ================================================================
             * STATISTICS
             * ================================================================ */
            w.write("============================================================\n");
            w.write("                           STATISTICS\n");
            w.write("============================================================\n\n");

            var mostCorrect = history.getMostCorrect();
            var mostIncorrect = history.getMostIncorrect();

            if (mostCorrect != null)
                w.write(String.format("Most Correct Answers   : %s (%d)%n",
                        mostCorrect.getKey(), mostCorrect.getValue()));

            if (mostIncorrect != null)
                w.write(String.format("Most Incorrect Answers : %s (%d)%n",
                        mostIncorrect.getKey(), mostIncorrect.getValue()));

            w.write("\nAccuracy Breakdown:\n");
            w.write(String.format("%-18s %-10s %-10s %-12s%n",
                    "Player", "Correct", "Incorrect", "Accuracy"));
            w.write("------------------------------------------------------------\n");

            for (String player : finalScores.keySet()) {
                w.write(String.format("%-18s %-10d %-10d %-12s%n",
                        player,
                        correctCounts.getOrDefault(player, 0),
                        incorrectCounts.getOrDefault(player, 0),
                        String.format("%.2f%%", accuracyMap.getOrDefault(player, 0.0))));
            }

            w.write("\n");


            /* ================================================================
             * TURN-BY-TURN HISTORY
             * ================================================================ */
            w.write("============================================================\n");
            w.write("                      TURN-BY-TURN HISTORY\n");
            w.write("============================================================\n\n");

            int turn = 1;

            for (GameRecord r : history.getRecords()) {

                w.write("------------------------------------------------------------\n");
                w.write("TURN " + (turn++) + "\n");
                w.write("------------------------------------------------------------\n");

                w.write(String.format("%-16s : %s%n", "Player", r.getPlayerName()));
                w.write(String.format("%-16s : %s%n", "Category", r.getCategory()));
                w.write(String.format("%-16s : %d%n", "Value", r.getValue()));
                w.write(String.format("%-16s : %s%n", "Question", wrap(r.getQuestionText())));
                w.write(String.format("%-16s : %s%n", "Player Answer", wrap(r.getUserAnswer())));
                w.write(String.format("%-16s : %s%n", "Correct Answer", wrap(r.getCorrectAnswer())));
                w.write(String.format("%-16s : %s%n", "Result", r.isCorrect() ? "CORRECT" : "INCORRECT"));
                w.write(String.format("%-16s : %d%n", "Points Earned", r.getPointsEarned()));
                w.write(String.format("%-16s : %d%n%n", "Running Score", r.getRunningScore()));
            }


            /* ================================================================
             * END OF REPORT
             * ================================================================ */
            w.write("============================================================\n");
            w.write("                          END OF REPORT\n");
            w.write("============================================================\n");
        }
    }

    
    /**
     * Wraps a long string into multiple lines for text-based reports.
     * Simple wrapping for long text 
     * <p>
     * Lines longer than {@link #WRAP_WIDTH} are broken and indented for readability.
     *
     * @param text the input text to wrap
     * @return the wrapped text
     */
    private String wrap(String text) {
        if (text.length() <= WRAP_WIDTH) return text;

        StringBuilder sb = new StringBuilder();
        int idx = 0;

        while (idx < text.length()) {
            int end = Math.min(idx + WRAP_WIDTH, text.length());
            sb.append(text, idx, end);
            if (end < text.length()) sb.append("\n                    ");
            idx += WRAP_WIDTH;
        }
        return sb.toString();
    }
}
