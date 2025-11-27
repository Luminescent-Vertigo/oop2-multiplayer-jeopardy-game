package oop2.jeopardy.report;

import oop2.jeopardy.game.GameHistory;
import oop2.jeopardy.game.GameRecord;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PdfReportGeneratorTest {

    private GameHistory createHistory() {
        GameHistory history = new GameHistory();

        history.add(new GameRecord(
                "Alice", "Math", 100,
                "2 + 2?", "A", "A",
                true, 100, 100
        ));

        history.add(new GameRecord(
                "Bob", "Science", 200,
                "Earth is?", "B", "A",
                false, -200, -200
        ));

        return history;
    }

    @Test
    public void testPdfReportIsCreated() throws Exception {
        GameHistory history = createHistory();
        PdfReportGenerator generator = new PdfReportGenerator();

        String filename = "target/test_report.pdf";
        File file = new File(filename);

        if (file.exists()) {
            file.delete();
        }

        generator.generate(history, filename);

        assertTrue(file.exists(), "PDF file should be created");
        assertTrue(file.length() > 0, "PDF file should not be empty");

        file.delete();
    }

    @Test
    public void testPdfReportWithEmptyHistory() throws Exception {
        GameHistory history = new GameHistory();
        PdfReportGenerator generator = new PdfReportGenerator();

        String filename = "target/empty_report.pdf";
        File file = new File(filename);

        if (file.exists()) {
            file.delete();
        }

        generator.generate(history, filename);

        assertTrue(file.exists(), "PDF must still be generated for empty history");
        assertTrue(file.length() > 0, "PDF must not be empty");

        file.delete();
    }

    @Test
    public void testFilenameWithoutExtension() throws Exception {
        GameHistory history = createHistory();
        PdfReportGenerator generator = new PdfReportGenerator();

        String filename = "target/no_extension_report";
        File file = new File(filename + ".pdf");

        if (file.exists()) {
            file.delete();
        }

        generator.generate(history, filename);

        assertTrue(file.exists(), "Generator must auto-append .pdf");

        file.delete();
    }

    @Test
    public void testLargeHistoryCreatesMultiplePages() throws Exception {
        GameHistory history = new GameHistory();

        for (int i = 1; i <= 50; i++) {
            history.add(new GameRecord(
                    "Player" + i,
                    "Category" + i,
                    100,
                    "Question number " + i,
                    "A",
                    "A",
                    true,
                    100,
                    i * 100
            ));
        }

        PdfReportGenerator generator = new PdfReportGenerator();

        String filename = "target/large_report.pdf";
        File file = new File(filename);

        if (file.exists()) {
            file.delete();
        }

        generator.generate(history, filename);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        file.delete();
    }

    @Test
    public void testNullHistoryThrowsException() {
        PdfReportGenerator generator = new PdfReportGenerator();

        assertThrows(NullPointerException.class, () ->
                generator.generate(null, "target/null.pdf"));
    }
}
