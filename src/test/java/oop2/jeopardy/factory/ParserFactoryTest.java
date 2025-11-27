package oop2.jeopardy.factory;

import oop2.jeopardy.parser.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserFactoryTest {

    @Test
    public void testReturnsJsonParser() {
        QuestionParser p = ParserFactory.getParser("json");
        assertTrue(p instanceof JsonParser);
    }

    @Test
    public void testReturnsCsvParser() {
        QuestionParser p = ParserFactory.getParser("csv");
        assertTrue(p instanceof CsvParser);
    }

    @Test
    public void testReturnsXmlParser() {
        QuestionParser p = ParserFactory.getParser("xml");
        assertTrue(p instanceof XmlParser);
    }

    @Test
    public void testCaseInsensitive() {
        QuestionParser p = ParserFactory.getParser("JsOn");
        assertTrue(p instanceof JsonParser);
    }

    @Test
    public void testUnsupportedFormatThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ParserFactory.getParser("txt"));
    }
}
