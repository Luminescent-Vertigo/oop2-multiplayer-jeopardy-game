package oop2.jeopardy.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    /* -----------------------------------------
     * Constructor validation
     * ----------------------------------------- */
    @Test
    public void testNameCannotBeBlankOrNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player(""));
        assertThrows(IllegalArgumentException.class,
                () -> new Player("   "));
        assertThrows(IllegalArgumentException.class,
                () -> new Player(null));
    }

    /* -----------------------------------------
     * Score addition
     * ----------------------------------------- */
    @Test
    public void testAddScore() {
        Player p = new Player("Bob");
        p.addScore(100);
        assertEquals(100, p.getScore());

        p.addScore(50);
        assertEquals(150, p.getScore());
    }

    /* -----------------------------------------
     * Deduction behavior — stays ≥ 0
     * ----------------------------------------- */
    @Test
    public void testDeductScoreFloorZero() {
        Player p = new Player("Bob");
        p.addScore(50);

        p.deductScore(20);
        assertEquals(30, p.getScore());

        // Should not go negative
        p.deductScore(1000);
        assertEquals(0, p.getScore(), "Score cannot be negative");
    }

    /* -----------------------------------------
     * equals() and hashCode() contract
     * ----------------------------------------- */
    @Test
    public void testEqualsAndHashCode() {
        Player p1 = new Player("Alice");
        Player p2 = new Player("Alice");
        Player p3 = new Player("Bob");

        // equality
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);

        // hash consistency
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    /* -----------------------------------------
     * toString() should contain player name + score
     * ----------------------------------------- */
    @Test
    public void testToStringFormat() {
        Player p = new Player("Charlie");
        p.addScore(100);

        String s = p.toString();
        assertTrue(s.contains("Charlie"));
        assertTrue(s.contains("100"));
    }
}
