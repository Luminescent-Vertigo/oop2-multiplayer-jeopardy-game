package oop2.jeopardy.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {

    @Test
    public void testConstructorAndGetters() {
        Map<String, String> opts = Map.of(
                "A", "OptA",
                "B", "OptB",
                "C", "OptC",
                "D", "OptD"
        );

        Question q = new Question(
                "History",
                200,
                "Who was the first president?",
                opts,
                "A"
        );

        assertEquals("History", q.getCategory());
        assertEquals(200, q.getValue());
        assertEquals("Who was the first president?", q.getQuestion());
        assertEquals(opts, q.getOptions());
        assertEquals("A", q.getCorrectAnswer());
    }

    @Test
    public void testUsedFlagDefaultAndSetter() {
        Question q = new Question(
                "Math",
                100,
                "2+2?",
                Map.of("A","1","B","2","C","3","D","4"),
                "D"
        );

        assertFalse(q.isUsed(), "New questions must start unused");

        q.setUsed(true);
        assertTrue(q.isUsed());
    }

    @Test
    public void testOptionsMustHaveFourEntries() {
        Map<String, String> opts = Map.of(
                "A", "1",
                "B", "2",
                "C", "3",
                "D", "4"
        );

        Question q = new Question("Math", 100, "2+2?", opts, "D");

        assertEquals(4, q.getOptions().size());
        assertTrue(q.getOptions().containsKey("A"));
        assertTrue(q.getOptions().containsKey("B"));
        assertTrue(q.getOptions().containsKey("C"));
        assertTrue(q.getOptions().containsKey("D"));
    }
}
