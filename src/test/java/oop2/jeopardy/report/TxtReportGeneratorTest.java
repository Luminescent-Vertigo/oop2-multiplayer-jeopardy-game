package oop2.jeopardy.report;

import oop2.jeopardy.game.GameHistory;
import oop2.jeopardy.game.GameRecord;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TxtReportGeneratorTest {

    private GameHistory sampleHistory() {
        GameHistory history = new GameHistory();

        history.add(new GameRecord(
                "Alice",
                "Science",
                100,
                "What is H2O?",
                "A",
                "A",
                true,
                100,
                100
        ));

        history.add(new GameRecord(
                "Bob",
                "Geography",
                200,
                "Capital of France?",
                "B",
                "A",
                false,
                -200,
                -200
        ));

        return history;
    }

    // ---------------------------------------------------------
    // 1. Basic TXT file creation
    // ---------------------------------------------------------
    @Test
    public void testTxtReportIsCreated() throws Exception {
        TxtReportGenerator generator = new TxtReportGenerator();
        GameHistory history = sampleHistory();

        String filename = "test_report.txt";
        File file = new File(filename);

        if (file.exists()) file.delete();

        generator.generate(history, filename);

        assertTrue(file.exists(), "TXT report should be created");
        assertTrue(file.length() > 0, "TXT file should not be empty");

        file.delete();
    }

    // ---------------------------------------------------------
    // 2. Empty history still creates report
    // ---------------------------------------------------------
    @Test
    public void testEmptyHistoryGeneratesReport() throws Exception {
        TxtReportGenerator generator = new TxtReportGenerator();
        GameHistory history = new GameHistory();

        String filename = "empty_report.txt";
        File file = new File(filename);

        if (file.exists()) file.delete();

        generator.generate(history, filename);

        assertTrue(file.exists(), "TXT report must be created even if history is empty");
        assertTrue(file.length() > 0, "TXT should at least include headers");

        file.delete();
    }

    // ---------------------------------------------------------
    // 3. Filename without extension adds '.txt'
    // ---------------------------------------------------------
    @Test
    public void testFilenameWithoutExtension() throws Exception {
        TxtReportGenerator generator = new TxtReportGenerator();
        GameHistory history = sampleHistory();

        String filename = "report_no_ext";
        File file = new File(filename + ".txt");

        if (file.exists()) file.delete();

        generator.generate(history, filename);

        assertTrue(file.exists(), "Generator should append .txt automatically");
        assertTrue(file.length() > 0);

        file.delete();
    }

    // ---------------------------------------------------------
    // 4. Larger history produces larger report
    // ---------------------------------------------------------
    @Test
    public void testLargeHistoryProducesLargerFile() throws Exception {
        TxtReportGenerator generator = new TxtReportGenerator();
        GameHistory history = new GameHistory();

        for (int i = 1; i <= 20; i++) {
            history.add(new GameRecord(
                    "Player" + i,
                    "Category" + i,
                    100,
                    "Long question " + i + " with a lot of extra sample text to test wrapping.",
                    "A",
                    "A",
                    true,
                    100,
                    i * 100
            ));
        }

        String filename = "large_history_report.txt";
        File file = new File(filename);

        if (file.exists()) file.delete();

        generator.generate(history, filename);

        assertTrue(file.exists(), "File must be created");
        assertTrue(file.length() > 500, "Large TXT report should be significantly bigger");

        file.delete();
    }

    // ---------------------------------------------------------
    // 5. Null history throws exception
    // ---------------------------------------------------------
    @Test
    public void testNullHistoryThrowsException() {
        TxtReportGenerator generator = new TxtReportGenerator();
        assertThrows(NullPointerException.class,
                () -> generator.generate(null, "null_history.txt"));
    }

    @Test
    public void testNullFilenameThrowsException() {
        TxtReportGenerator generator = new TxtReportGenerator();
        GameHistory history = new GameHistory();

        assertThrows(NullPointerException.class, () ->
                generator.generate(history, null));
    }

}
