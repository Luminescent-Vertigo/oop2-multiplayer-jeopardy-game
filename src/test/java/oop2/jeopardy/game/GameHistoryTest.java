package oop2.jeopardy.game;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameHistoryTest {

    /** Helper to generate GameRecord objects easily */
    private GameRecord rec(String player, boolean correct, int runningScore) {
        return new GameRecord(
                player,
                "Cat",
                100,
                "Q?",
                "A",
                "A",
                correct,
                correct ? 100 : -100,
                runningScore
        );
    }

    /* ============================================================
     * BASIC STORAGE
     * ============================================================ */
    @Test
    public void testAddAndSize() {
        GameHistory h = new GameHistory();

        assertTrue(h.isEmpty());

        h.add(rec("Bob", true, 100));
        h.add(rec("Alice", false, 0));

        assertEquals(2, h.size());
        assertFalse(h.isEmpty());
        assertEquals(2, h.getRecords().size());
    }

    /* ============================================================
     * FINAL SCORES (sorted descending)
     * ============================================================ */
    @Test
    public void testFinalScoresSortDescending() {
        GameHistory h = new GameHistory();

        h.add(rec("Bob", true, 400));
        h.add(rec("Alice", true, 500));
        h.add(rec("Zoe", false, 100));

        Map<String, Integer> scores = h.getFinalScores();

        assertEquals(List.of("Alice", "Bob", "Zoe"),
                     scores.keySet().stream().toList());
    }

    /* ============================================================
     * CORRECT COUNTS
     * ============================================================ */
    @Test
    public void testCorrectCounts() {
        GameHistory h = new GameHistory();

        h.add(rec("A", true, 100));
        h.add(rec("A", true, 200));
        h.add(rec("B", false, 0));

        Map<String, Integer> map = h.getCorrectCounts();

        assertEquals(2, map.get("A"));
        assertFalse(map.containsKey("B"));
    }

    /* ============================================================
     * INCORRECT COUNTS
     * ============================================================ */
    @Test
    public void testIncorrectCounts() {
        GameHistory h = new GameHistory();

        h.add(rec("X", false, 0));
        h.add(rec("X", false, -100));
        h.add(rec("Y", true, 100));

        Map<String, Integer> map = h.getIncorrectCounts();

        assertEquals(2, map.get("X"));
        assertFalse(map.containsKey("Y"));
    }

    /* ============================================================
     * ACCURACY MAP
     * ============================================================ */
    @Test
    public void testAccuracy() {
        GameHistory h = new GameHistory();

        h.add(rec("A", true, 100));   // 1 correct
        h.add(rec("A", false, 0));    // 1 wrong
        h.add(rec("B", true, 100));   // 1 correct, 0 wrong

        Map<String, Double> acc = h.getAccuracyMap();

        assertEquals(50.0, acc.get("A"));
        assertEquals(100.0, acc.get("B"));
    }

    /* ============================================================
     * MOST CORRECT
     * ============================================================ */
    @Test
    public void testMostCorrect() {
        GameHistory h = new GameHistory();

        h.add(rec("A", true, 100));
        h.add(rec("A", true, 200));
        h.add(rec("B", true, 100));

        var mc = h.getMostCorrect();

        assertEquals("A", mc.getKey());
        assertEquals(2, mc.getValue());
    }

    @Test
    public void testMostCorrect_WhenNoRecords() {
        GameHistory h = new GameHistory();

        assertNull(h.getMostCorrect(), "Must return null if no data exists");
    }

    /* ============================================================
     * MOST INCORRECT
     * ============================================================ */
    @Test
    public void testMostIncorrect() {
        GameHistory h = new GameHistory();

        h.add(rec("Z", false, 0));
        h.add(rec("Z", false, 0));
        h.add(rec("Y", false, 0));

        var mi = h.getMostIncorrect();

        assertEquals("Z", mi.getKey());
        assertEquals(2, mi.getValue());
    }

    @Test
    public void testMostIncorrect_WhenNoRecords() {
        GameHistory h = new GameHistory();

        assertNull(h.getMostIncorrect(), "Must return null when empty");
    }
}
