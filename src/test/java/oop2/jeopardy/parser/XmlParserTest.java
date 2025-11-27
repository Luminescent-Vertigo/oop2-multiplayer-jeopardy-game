package oop2.jeopardy.parser;

import oop2.jeopardy.model.Question;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XmlParserTest {

    private final XmlParser parser = new XmlParser();

    // Successful load
    @Test
    public void testLoadXmlSuccess() throws Exception {
        List<Question> questions = parser.load("data/xml_tests/valid.xml");

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

    // Missing file
    @Test
    public void testMissingXmlFile() {
        assertThrows(FileNotFoundException.class, () ->
                parser.load("data/xml_tests/missing.xml"));
    }

    // Malformed XML
    @Test
    public void testMalformedXml() {
        assertThrows(Exception.class, () ->
                parser.load("data/xml_tests/malformed.xml"));
    }

    // Empty file
    @Test
    public void testEmptyXml() {
        assertThrows(Exception.class, () ->
                parser.load("data/xml_tests/empty.xml"));
    }

    // Missing fields
    @Test
    public void testMissingFields() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/xml_tests/missing_fields.xml"));
    }

    // Blank strings
    @Test
    public void testBlankFields() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/xml_tests/blank_fields.xml"));
    }

    // Invalid value
    @Test
    public void testInvalidValue() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/xml_tests/invalid_value.xml"));
    }

    // Wrong options count
    @Test
    public void testWrongOptionsCount() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/xml_tests/wrong_options_count.xml"));
    }

    // Invalid correct answer
    @Test
    public void testInvalidCorrectAnswer() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.load("data/xml_tests/invalid_correct_answer.xml"));
    }

    // Extra fields allowed (ignored)
    @Test
    public void testExtraFieldsAllowed() throws Exception {
        List<Question> questions = parser.load("data/xml_tests/extra_fields.xml");
        assertNotNull(questions);
        assertEquals(1, questions.size());
        assertEquals("Science", questions.get(0).getCategory());
        assertEquals("A", questions.get(0).getCorrectAnswer());
    }
}
