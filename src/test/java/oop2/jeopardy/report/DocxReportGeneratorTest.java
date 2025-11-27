package oop2.jeopardy.report;

import oop2.jeopardy.game.GameHistory;
import oop2.jeopardy.game.GameRecord;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DocxReportGeneratorTest {

    @Test
    public void testDocxReportIsCreated() throws Exception {
        DocxReportGenerator generator = new DocxReportGenerator();

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

        String filename = "test_report.docx";

        generator.generate(history, filename);

        File file = new File(filename);

        assertTrue(file.exists(), "DOCX report file should be created");
        assertTrue(file.length() > 0, "DOCX report should not be empty");

        // Cleanup
        file.delete();
    }

    @Test
    public void testEmptyGameHistoryStillGeneratesReport() throws Exception {
        DocxReportGenerator generator = new DocxReportGenerator();

        GameHistory history = new GameHistory();

        String filename = "empty_report.docx";

        generator.generate(history, filename);

        File file = new File(filename);

        assertTrue(file.exists(), "DOCX report should still be created even if history is empty");
        assertTrue(file.length() > 0, "DOCX file should still contain headers");

        // Cleanup
        file.delete();
    }

    @Test
    public void testFilenameWithoutExtension() throws Exception {
        DocxReportGenerator generator = new DocxReportGenerator();

        GameHistory history = new GameHistory();
        history.add(new GameRecord(
                "Bob",
                "History",
                200,
                "Who discovered America?",
                "B",
                "B",
                true,
                200,
                200
        ));

        String filename = "no_extension_name";   // no .docx

        generator.generate(history, filename);

        File file = new File(filename + ".docx");

        assertTrue(file.exists(), "Generator should append .docx automatically");
        assertTrue(file.length() > 0);

        // Cleanup
        file.delete();
    }

    @Test
    public void testMultipleRecordsGenerateLargerReport() throws Exception {
        DocxReportGenerator generator = new DocxReportGenerator();

        GameHistory history = new GameHistory();

        for (int i = 1; i <= 5; i++) {
            history.add(new GameRecord(
                    "Player" + i,
                    "Math",
                    i * 100,
                    "Question " + i,
                    "C",
                    "C",
                    true,
                    100,
                    i * 100
            ));
        }

        String filename = "multi_record_report.docx";

        generator.generate(history, filename);

        File file = new File(filename);

        assertTrue(file.exists());
        assertTrue(file.length() > 1000, "Multi-record report should be larger");

        // Cleanup
        file.delete();
    }

    @Test
    public void testNullHistoryThrowsException() {
        DocxReportGenerator generator = new DocxReportGenerator();
        assertThrows(NullPointerException.class, () -> generator.generate(null, "test.docx"));
    }

}
