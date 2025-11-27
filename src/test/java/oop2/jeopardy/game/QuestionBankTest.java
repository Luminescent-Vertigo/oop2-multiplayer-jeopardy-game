package oop2.jeopardy.game;

import oop2.jeopardy.model.Question;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionBankTest {

    private Question sample(String category, int value) {
        Map<String, String> opts = Map.of(
                "A", "Option A",
                "B", "Option B",
                "C", "Option C",
                "D", "Option D"
        );
        return new Question(category, value, "Test Question", opts, "A");
    }

    @Test
    public void testBankLoadsQuestions() {
        List<Question> list = List.of(
                sample("Math", 100),
                sample("Math", 200),
                sample("Science", 100)
        );

        QuestionBank bank = new QuestionBank(list);

        assertEquals(3, bank.size()); // total questions

        assertTrue(bank.hasQuestion("Math", 100));
        assertTrue(bank.hasQuestion("Math", 200));
        assertTrue(bank.hasQuestion("Science", 100));

        assertNotNull(bank.get("Math", 100));
        assertEquals("Math", bank.get("Math", 100).getCategory());

        assertEquals(Set.of("Math", "Science"), bank.getCategories());
        assertEquals(Set.of(100, 200), bank.getValuesInCategory("Math"));
    }

    @Test
    public void testEmptyBank() {
        QuestionBank bank = new QuestionBank(List.of());

        assertEquals(0, bank.size());
        assertTrue(bank.getCategories().isEmpty());
        assertFalse(bank.hasQuestion("Any", 100));
    }

    @Test
    public void testOverwritingValueInCategory() {
        Question q1 = sample("History", 100);
        Question q2 = sample("History", 100); // same category + value

        QuestionBank bank = new QuestionBank(List.of(q1, q2));

        assertEquals(1, bank.size(), "Duplicate value in same category must overwrite");

        assertEquals(q2.getQuestion(), bank.get("History", 100).getQuestion());
    }

    @Test
    public void testNullListDoesNotCrash() {
        QuestionBank bank = new QuestionBank(new ArrayList<>());
        assertEquals(0, bank.size());
    }

}
