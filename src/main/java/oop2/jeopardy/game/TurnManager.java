package oop2.jeopardy.game;

import java.util.List;

/**
 * Manages player turns in a round-robin fashion.
 * Ensures the correct player is returned for each turn
 * and supports advancing to the next player.
 * 
 * <p>Used by {@link Game} to control whose turn it is during gameplay.</p>
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class TurnManager {

    /** Immutable list of players in the game. */
    private final List<Player> players;

    /** Index of the current player in the list. */
    private int currentIndex = 0;


    /**
     * Constructs a {@code TurnManager} with the given list of players.
     *
     * @param players the list of players; must contain 1–4 players
     * @throws IllegalArgumentException if the list is null, empty, or exceeds 4 players
     */
    public TurnManager(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one player");
        }
        if (players.size() > 4) {
            throw new IllegalArgumentException("Maximum 4 players allowed");
        }
        this.players = List.copyOf(players);   // defensive copy
    }


    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current {@link Player}
     */
    public Player getCurrentPlayer() {
        return players.get(currentIndex);
    }


    /**
     * Advances to the next player in round-robin order.
     */
    public void next() {
        currentIndex = (currentIndex + 1) % players.size();
    }


    /**
     * Returns the number of players being managed.
     *
     * @return the player count
     */
    public int getPlayerCount() {
        return players.size();
    }
}
