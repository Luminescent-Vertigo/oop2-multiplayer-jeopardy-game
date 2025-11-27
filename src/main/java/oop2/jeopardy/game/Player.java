package oop2.jeopardy.game;

import java.util.Objects;

/**
 * Represents a player in the Jeopardy game.
 * Stores the player's name and current score, and provides
 * methods to modify the score and compare players.
 * 
 * <p>Used by {@link Game} and {@link TurnManager} to track player state.</p>
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class Player {

    /** The player's name. */
    private final String name;

    /** The player's current score. */
    private int score = 0;


    /**
     * Constructs a new {@code Player} with the given name.
     *
     * @param name the player's name; must not be null or blank
     * @throws IllegalArgumentException if name is null or blank
     */
    public Player(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        this.name = name.trim();
    }


    /** Get the player's name
     * 
     * @return the player's name */
    public String getName() {
        return name;
    }


    /** Get the player's current score
     * 
     * @return the player's current score */
    public int getScore() {
        return score;
    }


    /**
     * Adds points to the player's score.
     *
     * @param points the number of points to add
     */
    public void addScore(int points) {
        score += points;
    }


    /**
     * Deducts points from the player's score, ensuring the score
     * does not drop below zero.
     *
     * @param points the number of points to deduct
     */
    public void deductScore(int points) {
        score -= points;
        if (score < 0) {
            score = 0;   // STAYS >= 0 
        }
    }


    /**
     * Returns a string representation of the player.
     *
     * @return player's name and current score in parentheses
     */
    @Override
    public String toString() {
        return name + " (" + score + ")";
    }


    /**
     * Compares two players for equality based on name.
     *
     * @param o the object to compare with
     * @return true if the other object is a Player with the same name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player other)) return false;
        return Objects.equals(name, other.name);
    }

    
    /** Get the has code based on the player's name
     * 
     * @return the hash code based on the player's name */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
