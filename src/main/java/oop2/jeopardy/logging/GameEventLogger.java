package oop2.jeopardy.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles writing structured gameplay events to a CSV log file.
 * <p>
 * This class automatically:
 * <ul>
 *     <li>Maintains a persistent game counter (GAME001, GAME002, ...)</li>
 *     <li>Ensures the log directory and CSV header exist</li>
 *     <li>Appends event entries in a clean, comma–safe format</li>
 * </ul>
 * 
 * The output is stored under {@code reports/logs} by default, but this may be
 * overridden using the system property {@code log.folder.override}.
 *
 * <p>Used for audit trails, debugging, and producing game analytics reports.</p>
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class GameEventLogger {

    //private static final String LOG_FOLDER = "reports/logs";

    /** Base folder where logs are written. Can be overridden via JVM property. */
    private static final String LOG_FOLDER =
            System.getProperty("log.folder.override", "reports/logs");

    /** Name of the CSV file where events are recorded. */
    private static final String LOG_FILE = "game_event_log.csv";

    /** Timestamp format for all logged entries. */
    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /** Unique ID assigned to the current game session (GAME001, GAME002, ...). */
    private final String caseId;


    /**
     * Creates a new logger instance.
     * <p>
     * This constructor:
     * <ul>
     *     <li>Loads and increments the persistent game counter</li>
     *     <li>Generates a formatted case ID (e.g., GAME007)</li>
     *     <li>Ensures the CSV log file exists and has its header</li>
     * </ul>
     */
    public GameEventLogger() {
        int number = loadAndIncrementGameNumber();
        this.caseId = String.format("GAME%03d", number);
        ensureHeaderExists();
    }


    /**
     * Loads the game counter from {@code game_counter.txt}, increments it,
     * writes back the updated value, and returns the new game number.
     *
     * @return the incremented game number; defaults to {@code 1} on error
     */
    private int loadAndIncrementGameNumber() {

        File folder = new File(LOG_FOLDER);
        if (!folder.exists()) folder.mkdirs();

        File counterFile = new File(LOG_FOLDER + "/game_counter.txt");

        try {
            // If first time: create file with "0"
            if (!counterFile.exists()) {
                Files.writeString(counterFile.toPath(), "0");
                // First ever game becomes GAME001
                Files.writeString(counterFile.toPath(), "1");
                return 1;
            }

            // Read the existing number
            String content = Files.readString(counterFile.toPath()).trim();
            int current = Integer.parseInt(content);

            int next = current + 1;

            // Save incremented/new value
            Files.writeString(counterFile.toPath(), String.valueOf(next));

            return next;

        } catch (Exception e) {
            System.err.println("Error handling game counter: " + e.getMessage());
            return 1; // fallback so system keeps working
        }
    }


    /**
     * Ensures the CSV log file exists and contains the required header row.
     * <p>
     * If the file does not exist, it is created with the following columns:
     * <pre>
     * Case_ID,Player_ID,Activity,Timestamp,Category,
     * Question_Value,Answer_Given,Result,Score_After_Play
     * </pre>
     */
    private void ensureHeaderExists() {
        try {
            File dir = new File(LOG_FOLDER);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(LOG_FOLDER + "/" + LOG_FILE);

            if (!file.exists()) {
                try (FileWriter w = new FileWriter(file, false)) {
                    w.write("Case_ID,Player_ID,Activity,Timestamp,Category,");
                    w.write("Question_Value,Answer_Given,Result,Score_After_Play\n");
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating event log header: " + e.getMessage());
        }
    }
    

    /**
     * Appends a single event entry to the CSV log.
     *
     * @param playerId    ID or name of the player performing the action
     * @param activity    description of the event (e.g., "SELECT", "ANSWERED", "RESULT")
     * @param category    question category (or blank)
     * @param questionValue value of the question (e.g., "200")
     * @param answer      the player's answer text
     * @param result      outcome: "CORRECT" or "WRONG"
     * @param scoreAfter  player's running score after the event
     */
    public void log(
            String playerId,
            String activity,
            String category,
            String questionValue,
            String answer,
            String result,
            String scoreAfter
    ) {

        String ts = LocalDateTime.now().format(TS_FORMAT);

        try (FileWriter w = new FileWriter(LOG_FOLDER + "/" + LOG_FILE, true)) {

            w.write(String.join(",",
                    safe(caseId),
                    safe(playerId),
                    safe(activity),
                    safe(ts),
                    safe(category),
                    safe(questionValue),
                    safe(answer),
                    safe(result),
                    safe(scoreAfter)
            ));
            w.write("\n");

        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }


    /**
     * Sanitizes a string for CSV output by replacing commas with spaces
     * and converting {@code null} values to empty strings.
     *
     * @param s the string to sanitize
     * @return a safe CSV-compatible string
     */
    private String safe(String s) {
        return s == null ? "" : s.replace(",", " ");
    }
}
