package oop2.jeopardy.game;

import oop2.jeopardy.model.Question;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    /** Helper to build a minimal valid Question */
    private Question q(String cat, int value) {
        return new Question(
                cat,
                value,
                "Q?",
                Map.of("A", "x", "B", "y", "C", "z", "D", "w"),
                "A"
        );
    }

    /* ============================================================
     * CONSTRUCTOR TESTS
     * ============================================================ */

    @Test
    public void testConstructorRejectsEmptyList() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(List.of()));
    }

    @Test
    public void testConstructorRejectsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(null));
    }

    @Test
    public void testConstructorGroupsAndSortsQuestions() {
        List<Question> list = List.of(
                q("Math", 300),
                q("Math", 100),
                q("Math", 200),
                q("Sci", 500)
        );

        GameBoard board = new GameBoard(list);

        assertEquals(List.of("Math", "Sci"), board.getCategories());
        assertEquals(List.of(100, 200, 300), board.getAvailableValues("Math"));
        assertEquals(List.of(500), board.getAvailableValues("Sci"));
    }

    @Test
    public void testConstructorPreservesCategoryInsertionOrder() {
        List<Question> list = List.of(
                q("Math", 200),
                q("Sci", 100),
                q("Math", 100)
        );

        GameBoard board = new GameBoard(list);

        // Math appears first, Science second
        assertEquals(List.of("Math", "Sci"), board.getCategories());
    }

    /* ============================================================
     * AVAILABLE VALUES & RETRIEVAL
     * ============================================================ */

    @Test
    public void testGetAvailableValuesForUnknownCategory() {
        GameBoard board = new GameBoard(List.of(q("Math", 100)));

        assertEquals(Collections.emptyList(),
                board.getAvailableValues("Unknown"));
    }

    @Test
    public void testGetQuestionOnlyReturnsUnused() {
        Question q1 = q("History", 100);
        GameBoard board = new GameBoard(List.of(q1));

        assertNotNull(board.getQuestion("History", 100));

        board.markUsed(q1);

        assertNull(board.getQuestion("History", 100),
                "Used question must not be returned");
    }

    @Test
    public void testGetQuestionMissingValueReturnsNull() {
        GameBoard board = new GameBoard(List.of(q("Math", 100)));

        assertNull(board.getQuestion("Math", 999));
    }

    /* ============================================================
     * DUPLICATE VALUE BEHAVIOR (EDGE CASE)
     * ============================================================ */

    @Test
    public void testDuplicateValuesInSameCategoryReturnsNextUnused() {
        Question q1 = q("Math", 100);
        Question q2 = q("Math", 100); // duplicate value

        GameBoard board = new GameBoard(List.of(q1, q2));

        // First retrieval should return the first unused
        Question first = board.getQuestion("Math", 100);
        assertNotNull(first);

        board.markUsed(first);

        // Next retrieval should return the remaining one
        Question second = board.getQuestion("Math", 100);
        assertNotNull(second);
        assertNotSame(first, second);
    }

    /* ============================================================
     * STATE TRANSITIONS
     * ============================================================ */

    @Test
    public void testMarkUsed() {
        Question q1 = q("Geo", 200);
        GameBoard board = new GameBoard(List.of(q1));

        assertFalse(q1.isUsed());

        board.markUsed(q1);

        assertTrue(q1.isUsed());
    }

    @Test
    public void testMarkUsedNullDoesNotCrash() {
        GameBoard board = new GameBoard(List.of(q("X", 100)));
        assertDoesNotThrow(() -> board.markUsed(null));
    }

    @Test
    public void testRemainingCount() {
        Question q1 = q("C", 100);
        Question q2 = q("C", 200);
        GameBoard board = new GameBoard(List.of(q1, q2));

        assertEquals(2, board.remainingCount());

        board.markUsed(q1);
        assertEquals(1, board.remainingCount());

        board.markUsed(q2);
        assertEquals(0, board.remainingCount());
    }

    @Test
    public void testIsFinished() {
        Question q1 = q("X", 100);
        Question q2 = q("X", 200);
        GameBoard board = new GameBoard(List.of(q1, q2));

        assertFalse(board.isFinished());
        board.markUsed(q1);
        assertFalse(board.isFinished());
        board.markUsed(q2);
        assertTrue(board.isFinished());
    }

    @Test
    public void testIsFinishedMultiCategory() {
        Question q1 = q("A", 100);
        Question q2 = q("B", 200);
        GameBoard board = new GameBoard(List.of(q1, q2));

        assertFalse(board.isFinished());

        board.markUsed(q1);
        assertFalse(board.isFinished());

        board.markUsed(q2);
        assertTrue(board.isFinished());
    }
}
