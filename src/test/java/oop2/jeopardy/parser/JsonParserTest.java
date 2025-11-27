package oop2.jeopardy.parser;

import oop2.jeopardy.model.Question;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    private final JsonParser parser = new JsonParser();

    // ---------------------------
    // SUCCESS CASE
    // ---------------------------
    @Test
    public void testLoadJsonSuccess() throws Exception {
        List<Question> questions = parser.load("data/json_tests/valid.json");

        assertNotNull(questions);
        assertFalse(questions.isEmpty());

        for (Question q : questions) {
            assertNotNull(q.getCategory());
            assertFalse(q.getCategory().isBlank());

            assertTrue(q.getValue() > 0);

            assertNotNull(q.getQuestion());
            assertFalse(q.getQuestion().isBlank());

            assertNotNull(q.getOptions());
            assertEquals(4, q.getOptions().size());

            assertTrue(q.getOptions().containsKey("A"));
            assertTrue(q.getOptions().containsKey("B"));
            assertTrue(q.getOptions().containsKey("C"));
            assertTrue(q.getOptions().containsKey("D"));

            assertNotNull(q.getCorrectAnswer());
            assertTrue("ABCD".contains(q.getCorrectAnswer()));
        }
    }

    // ---------------------------
    // MISSING FILE
    // ---------------------------
    @Test
    public void testMissingJsonFile() {
        assertThrows(FileNotFoundException.class, () ->
                parser.load("data/json_tests/does_not_exist.json")
        );
    }

    // ---------------------------
    // MALFORMED JSON
    // ---------------------------
    @Test
    public void testMalformedJson() {
        assertThrows(Exception.class, () ->
                parser.load("data/json_tests/malformed.json")
        );
    }

    // ---------------------------
    // NULL TOP LEVEL JSON
    // ---------------------------
    @Test
    public void testNullJsonRoot() {
        assertThrows(Exception.class, () ->
                parser.load("data/json_tests/null_content.json")
        );
    }

    // ---------------------------
    // EMPTY JSON ARRAY
    // ---------------------------
    @Test
    public void testEmptyArray() throws Exception {
        List<Question> questions = parser.load("data/json_tests/empty_array.json");
        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    // ---------------------------
    // MISSING FIELD
    // ---------------------------
    @Test
    public void testMissingField() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/json_tests/missing_field.json")
        );
    }

    // ---------------------------
    // WRONG TYPE (value not an int)
    // ---------------------------
    @Test
    public void testWrongType() {
        assertThrows(Exception.class, () ->
                parser.load("data/json_tests/wrong_type.json")
        );
    }

    // ---------------------------
    // WRONG OPTIONS SIZE
    // ---------------------------
    @Test
    public void testWrongOptionsSize() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/json_tests/options_wrong_size.json")
        );
    }

    // ---------------------------
    // INVALID CORRECT ANSWER
    // ---------------------------
    @Test
    public void testInvalidCorrectAnswer() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/json_tests/invalid_correct_answer.json")
        );
    }

    // ---------------------------
    // BLANK STRINGS
    // ---------------------------
    @Test
    public void testBlankFields() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/json_tests/blank_strings.json")
        );
    }

    // ---------------------------
    // EXTRA FIELDS (allowed)
    // ---------------------------
    @Test
    public void testExtraFieldsAllowed() throws Exception {
        List<Question> questions = parser.load("data/json_tests/extra_fields.json");
        assertNotNull(questions);
        assertEquals(1, questions.size());

        Question q = questions.get(0);
        assertEquals("Science", q.getCategory());
        assertEquals("A", q.getCorrectAnswer());
    }
}
