package oop2.jeopardy.logging;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameEventLoggerTest {

    private static final String TEMP_DIR = "test_logs";

    @BeforeEach
    public void setup() throws IOException {
        // Clean temp test directory before each test
        Path testDir = Paths.get(TEMP_DIR);
        if (Files.exists(testDir)) {
            Files.walk(testDir)
                    .map(Path::toFile)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(File::delete);
        }
        Files.createDirectories(testDir);

        // Redirect logger folder to our test directory
        System.setProperty("log.folder.override", TEMP_DIR);
    }

    @Test
    public void testLoggerCreatesFiles() throws Exception {
        // Create logger
        @SuppressWarnings("unused")
        GameEventLogger logger = new GameEventLogger();

        // Ensure folder exists
        File dir = new File(TEMP_DIR);
        assertTrue(dir.exists());

        // Ensure CSV exists
        File csv = new File(TEMP_DIR + "/game_event_log.csv");
        assertTrue(csv.exists(), "CSV log file should be created");

        // Ensure header exists
        List<String> lines = Files.readAllLines(csv.toPath());
        assertFalse(lines.isEmpty(), "CSV file should have header");
        assertEquals(
                "Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play",
                lines.get(0)
        );
    }

    @Test
    public void testGameCounterIncrements() throws Exception {
        @SuppressWarnings("unused")
        GameEventLogger logger1 = new GameEventLogger();
        @SuppressWarnings("unused")
        GameEventLogger logger2 = new GameEventLogger();

        // Read the game_counter.txt
        String content = Files.readString(Path.of(TEMP_DIR, "game_counter.txt")).trim();
        int value = Integer.parseInt(content);

        assertEquals(2, value, "Counter should increment to 2 after two loggers");
    }

    @Test
    public void testLogEntryWritten() throws Exception {
        GameEventLogger logger = new GameEventLogger();

        logger.log("P1", "Start Game", "Math", "100", "4", "Correct", "0");

        File csv = new File(TEMP_DIR + "/game_event_log.csv");
        List<String> lines = Files.readAllLines(csv.toPath());

        assertEquals(2, lines.size(), "Header + one entry expected");

        // Validate entry structure
        String entry = lines.get(1);
        String[] parts = entry.split(",");

        assertEquals(9, parts.length, "Log entry must have 9 columns");

        assertTrue(parts[0].matches("GAME\\d{3}"), "Case_ID must match GAME### format");
        assertEquals("P1", parts[1]);
        assertEquals("Start Game", parts[2]);
        // Timestamp can't be checked exactly, but ensure it's non-empty
        assertFalse(parts[3].isBlank());
        assertEquals("Math", parts[4]);
        assertEquals("100", parts[5]);
        assertEquals("4", parts[6]);
        assertEquals("Correct", parts[7]);
        assertEquals("0", parts[8]);
    }
}
