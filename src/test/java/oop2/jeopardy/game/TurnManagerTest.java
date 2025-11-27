package oop2.jeopardy.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TurnManagerTest {

    /* ============================================================
     * CONSTRUCTOR VALIDATION
     * ============================================================ */

    @Test
    public void testConstructorRejectsNullList() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(null));
    }

    @Test
    public void testConstructorRejectsEmptyList() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(List.of()));
    }

    @Test
    public void testConstructorRejectsMoreThanFourPlayers() {
        assertThrows(IllegalArgumentException.class,
                () -> new TurnManager(List.of(
                        new Player("A"), new Player("B"), new Player("C"),
                        new Player("D"), new Player("E")
                )));
    }

    /* ============================================================
     * PLAYER COUNT
     * ============================================================ */

    @Test
    public void testPlayerCount() {
        TurnManager tm = new TurnManager(List.of(
                new Player("A"), new Player("B")
        ));
        assertEquals(2, tm.getPlayerCount());
    }

    /* ============================================================
     * TURN ROTATION
     * ============================================================ */

    @Test
    public void testTurnRotationWrapsCorrectly() {
        Player a = new Player("A");
        Player b = new Player("B");
        Player c = new Player("C");

        TurnManager tm = new TurnManager(List.of(a, b, c));

        assertEquals(a, tm.getCurrentPlayer());
        tm.next();
        assertEquals(b, tm.getCurrentPlayer());
        tm.next();
        assertEquals(c, tm.getCurrentPlayer());
        tm.next();
        assertEquals(a, tm.getCurrentPlayer(), "Should wrap to first player");
    }

    @Test
    public void testRotationRepeatCyclesCorrectly() {
        Player a = new Player("A");
        Player b = new Player("B");

        TurnManager tm = new TurnManager(List.of(a, b));

        // Cycle through many times to ensure stability
        for (int i = 0; i < 10; i++) {
            assertEquals(i % 2 == 0 ? a : b, tm.getCurrentPlayer());
            tm.next();
        }
    }

    /* ============================================================
     * DEFENSIVE COPY & IMMUTABILITY
     * ============================================================ */

    @Test
    public void testPassedListDoesNotAffectTurnManager() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("A"));
        players.add(new Player("B"));

        TurnManager tm = new TurnManager(players);

        // Modify original list
        players.add(new Player("C"));

        // TurnManager should be unaffected
        assertEquals(2, tm.getPlayerCount());
        assertEquals("A", tm.getCurrentPlayer().getName());
    }
}
