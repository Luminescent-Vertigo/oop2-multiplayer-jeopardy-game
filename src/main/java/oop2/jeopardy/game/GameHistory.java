package oop2.jeopardy.game;

import java.util.*;

/**
 * Maintains a complete history of all game actions and player results.
 * Provides utility methods for retrieving final scores, counting correct
 * and incorrect answers, calculating accuracy, and identifying top performers.
 * 
 * <p>This class is primarily used by {@link Game} for reporting, scoreboard
 * generation, and statistical analysis.</p>
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class GameHistory {

    /** Internal storage of all game records. */
    private final List<GameRecord> records = new ArrayList<>();
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    public GameHistory() {
        // Prevent direct instantiation
    }
    

    /* ============================================================
     * BASIC STORAGE
     * ============================================================ */

    /**
     * Adds a {@link GameRecord} to the game history.
     *
     * @param rec the record to add; ignored if {@code null}
     */
    public void add(GameRecord rec) {
        if (rec != null) {
            records.add(rec);
        }
    }


    /**
     * Returns an unmodifiable list of all game records.
     *
     * @return list of {@link GameRecord}
     */
    public List<GameRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }


    /**
     * Returns the total number of records stored.
     *
     * @return number of game records
     */
    public int size() {
        return records.size();
    }


    /**
     * Checks whether any records exist in the history.
     *
     * @return {@code true} if empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return records.isEmpty();
    }


    /* ============================================================
     * FINAL SCORES (sorted descending)
     * ============================================================ */

    /**
     * Returns a map of final player scores, sorted in descending order.
     *
     * @return a {@link LinkedHashMap} with player names as keys and running scores as values
     */
    public Map<String, Integer> getFinalScores() {
        Map<String, Integer> scores = new LinkedHashMap<>();

        for (GameRecord r : records) {
            scores.put(r.getPlayerName(), r.getRunningScore());
        }

        return scores.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll);
    }


    /* ============================================================
     * COUNT CORRECT ANSWERS PER PLAYER
     * ============================================================ */

    /**
     * Counts the number of correct answers for each player.
     *
     * @return a {@link LinkedHashMap} mapping player names to correct answer counts
     */
    public Map<String, Integer> getCorrectCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (GameRecord r : records) {
            if (r.isCorrect()) {
                counts.merge(r.getPlayerName(), 1, Integer::sum);
            }
        }

        return counts;
    }


    /* ============================================================
     * COUNT INCORRECT ANSWERS PER PLAYER
     * ============================================================ */

    /**
     * Counts the number of incorrect answers for each player.
     *
     * @return a {@link LinkedHashMap} mapping player names to incorrect answer counts
     */
    public Map<String, Integer> getIncorrectCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (GameRecord r : records) {
            if (!r.isCorrect()) {
                counts.merge(r.getPlayerName(), 1, Integer::sum);
            }
        }

        return counts;
    }

    
    /* ============================================================
     * ACCURACY PERCENTAGE PER PLAYER
     * ============================================================ */

    /**
     * Calculates the accuracy percentage (correct answers / total answered)
     * for each player.
     *
     * @return a {@link LinkedHashMap} mapping player names to accuracy percentages
     */
    public Map<String, Double> getAccuracyMap() {
        Map<String, Integer> correct = getCorrectCounts();
        Map<String, Integer> incorrect = getIncorrectCounts();

        Map<String, Double> accuracy = new LinkedHashMap<>();

        for (String player : correct.keySet()) {
            int c = correct.get(player);
            int w = incorrect.getOrDefault(player, 0);
            int total = c + w;

            double pct = total == 0 ? 0 : (c * 100.0 / total);
            accuracy.put(player, pct);
        }

        return accuracy;
    }


    /* ============================================================
     * PLAYER WITH MOST CORRECT ANSWERS
     * ============================================================ */

    /**
     * Returns the player with the most correct answers.
     *
     * @return a {@link Map.Entry} containing the player's name and count,
     *         or {@code null} if no records exist
     */
    public Map.Entry<String, Integer> getMostCorrect() {
        return getCorrectCounts().entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }


    /* ============================================================
     * PLAYER WITH MOST INCORRECT ANSWERS
     * ============================================================ */

    /**
     * Returns the player with the most incorrect answers.
     *
     * @return a {@link Map.Entry} containing the player's name and count,
     *         or {@code null} if no records exist
     */
    public Map.Entry<String, Integer> getMostIncorrect() {
        return getIncorrectCounts().entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }
}

