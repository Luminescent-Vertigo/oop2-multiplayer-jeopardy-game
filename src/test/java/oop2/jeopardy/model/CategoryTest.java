package oop2.jeopardy.model;

//import oop2.jeopardy.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    private Question sampleQuestion() {
        return new Question(
                "Math",
                100,
                "What is 2+2?",
                Map.of("A", "3", "B", "4", "C", "5", "D", "6"),
                "B"
        );
    }

    @Test
    public void testConstructorAndGetters() {
        Question q = sampleQuestion();
        Category c = new Category("Math", List.of(q));

        assertEquals("Math", c.getName());
        assertEquals(1, c.getQuestions().size());
        assertEquals(q, c.getQuestions().get(0));
    }

    @Test
    public void testToStringReturnsName() {
        Category c = new Category("Science", List.of());
        assertEquals("Science", c.toString());
    }
}
