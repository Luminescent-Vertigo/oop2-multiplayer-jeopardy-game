package oop2.jeopardy.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameRecordTest {

    @Test
    public void testConstructorStoresValuesCorrectly() {
        GameRecord rec = new GameRecord(
                "Alice",
                "Math",
                200,
                "What is 2+2?",
                "A",
                "A",
                true,
                200,
                400
        );

        assertEquals("Alice", rec.getPlayerName());
        assertEquals("Math", rec.getCategory());
        assertEquals(200, rec.getValue());
        assertEquals("What is 2+2?", rec.getQuestionText());
        assertEquals("A", rec.getUserAnswer());
        assertEquals("A", rec.getCorrectAnswer());
        assertTrue(rec.isCorrect());
        assertEquals(200, rec.getPointsEarned());
        assertEquals(400, rec.getRunningScore());
    }

    @Test
    public void testAllowsNullValues() {
        GameRecord rec = new GameRecord(
                null, null, 0, null,
                null, null,
                false,
                0,
                0
        );

        assertNull(rec.getPlayerName());
        assertNull(rec.getCategory());
        assertEquals(0, rec.getValue());
        assertNull(rec.getQuestionText());
        assertNull(rec.getUserAnswer());
        assertNull(rec.getCorrectAnswer());
        assertFalse(rec.isCorrect());
        assertEquals(0, rec.getPointsEarned());
        assertEquals(0, rec.getRunningScore());
    }

    @Test
    public void testToStringDoesNotCrash() {
        GameRecord rec = new GameRecord(
                "Bob",
                "Sci",
                500,
                "Q?",
                "B",
                "C",
                false,
                -500,
                0
        );

        String out = rec.toString();

        assertNotNull(out);
        assertTrue(out.contains("Bob"));
        assertTrue(out.contains("Sci"));
        assertTrue(out.contains("500"));
    }
}
