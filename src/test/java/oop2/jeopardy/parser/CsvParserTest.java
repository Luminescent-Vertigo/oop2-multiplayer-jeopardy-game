package oop2.jeopardy.parser;

import oop2.jeopardy.model.Question;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvParserTest {

    // ------------------------------------------------------
    // 1. Successful Parsing
    // ------------------------------------------------------
    @Test
    public void testLoadCsvSuccess() throws Exception {
        CsvParser parser = new CsvParser();

        List<Question> questions = parser.load("data/csv_tests/valid.csv");

        assertNotNull(questions);
        assertFalse(questions.isEmpty(), "CSV must contain questions");

        for (Question q : questions) {
            assertNotNull(q.getCategory());
            assertFalse(q.getCategory().isBlank());

            assertTrue(q.getValue() > 0, "Value must be positive");

            assertNotNull(q.getQuestion());
            assertFalse(q.getQuestion().isBlank());

            assertNotNull(q.getOptions());
            assertEquals(4, q.getOptions().size(), "There must be exactly 4 options");

            assertTrue(q.getOptions().containsKey("A"));
            assertTrue(q.getOptions().containsKey("B"));
            assertTrue(q.getOptions().containsKey("C"));
            assertTrue(q.getOptions().containsKey("D"));

            assertNotNull(q.getCorrectAnswer());
            assertTrue("ABCD".contains(q.getCorrectAnswer()),
                    "CorrectAnswer must be one of A/B/C/D");
        }
    }

    // ------------------------------------------------------
    // 2. Missing File
    // ------------------------------------------------------
    @Test
    public void testMissingCsvFile() {
        CsvParser parser = new CsvParser();

        assertThrows(FileNotFoundException.class, () ->
                parser.load("data/csv_tests/missing.csv"));
    }

    // ------------------------------------------------------
    // 3. Missing Required Columns
    // ------------------------------------------------------
    @Test
    public void testMissingRequiredColumns() {
        CsvParser parser = new CsvParser();

        Exception ex = assertThrows(Exception.class, () ->
                parser.load("data/csv_tests/missing_fields.csv"));

        assertTrue(ex.getMessage().contains("OptionA")
                || ex.getMessage().contains("Category")
                || ex.getMessage().contains("Value"));
    }

    // ------------------------------------------------------
    // 4. Blank Lines in CSV
    // ------------------------------------------------------
    @Test
    public void testCsvWithBlankLines() throws Exception {
        CsvParser parser = new CsvParser();

        List<Question> questions = parser.load("data/csv_tests/blank_lines.csv");

        assertNotNull(questions);
        assertFalse(questions.isEmpty(), "Blank lines should not break parsing");

        for (Question q : questions) {
            assertNotNull(q.getCategory());
        }
    }

    // ------------------------------------------------------
    // 5. Missing Option Fields (empty cells)
    // ------------------------------------------------------
    /*@Test
    public void testMissingOptionValues() throws Exception {
        CsvParser parser = new CsvParser();

        List<Question> questions = parser.load("data/csv_tests/missing_options.csv");

        for (Question q : questions) {
            assertEquals(4, q.getOptions().size());

            assertNotNull(q.getOptions().get("A"));
            assertNotNull(q.getOptions().get("B"));
            assertNotNull(q.getOptions().get("C"));
            assertNotNull(q.getOptions().get("D"));
        }
    }*/

    @Test
    public void testMissingOptionValues() {
        CsvParser parser = new CsvParser();

        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/csv_tests/missing_options.csv")
        );
    }


    // ------------------------------------------------------
    // 6. Invalid Numeric Value
    // ------------------------------------------------------
    @Test
    public void testInvalidValueNotInteger() {
        CsvParser parser = new CsvParser();

        assertThrows(NumberFormatException.class, () ->
                parser.load("data/csv_tests/invalid_value.csv"));
    }

    // ------------------------------------------------------
    // 7. Invalid CorrectAnswer (not A/B/C/D)
    // ------------------------------------------------------
    @Test
    public void testInvalidCorrectAnswer() {
        CsvParser parser = new CsvParser();

        assertThrows(Exception.class, () ->
                parser.load("data/csv_tests/invalid_correctanswer.csv"));
    }

    // ------------------------------------------------------
    // 8. Null Filename
    // ------------------------------------------------------
    @Test
    public void testNullFilename() {
        CsvParser parser = new CsvParser();
        assertThrows(NullPointerException.class, () ->
                parser.load(null));
    }

    // ------------------------------------------------------
    // 9. Extra Columns Should NOT Break Parsing
    // ------------------------------------------------------
    @Test
    public void testCsvWithExtraColumns() throws Exception {
        CsvParser parser = new CsvParser();

        List<Question> questions = parser.load("data/csv_tests/extra_columns.csv");

        assertNotNull(questions);
        assertFalse(questions.isEmpty(), "Should still load despite extra columns");
    }

    // ------------------------------------------------------
    // 10. Leading/Trailing Whitespace Handling
    // ------------------------------------------------------
    @Test
    public void testWhitespaceTrimming() throws Exception {
        CsvParser parser = new CsvParser();

        List<Question> questions = parser.load("data/csv_tests/whitespace.csv");

        assertNotNull(questions);
        assertFalse(questions.isEmpty());

        for (Question q : questions) {
            assertFalse(q.getCategory().isBlank());
            assertFalse(q.getQuestion().isBlank());
        }
    }

    // ------------------------------------------------------
    // 11. Blank Fields (should now FAIL)
    // ------------------------------------------------------
    @Test
    public void testBlankFields() {
        CsvParser parser = new CsvParser();
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/csv_tests/blank_fields.csv"));
    }

    // ------------------------------------------------------
    // 12. Options Wrong Size (cannot happen in CSV, but blank values still fail)
    // ------------------------------------------------------
    @Test
    public void testOptionsWrongSize() {
        CsvParser parser = new CsvParser();
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/csv_tests/options_wrong_size.csv"));
    }
}
