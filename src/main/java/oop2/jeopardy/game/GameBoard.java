package oop2.jeopardy.game;

import oop2.jeopardy.model.Question;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the game board, storing questions organized by category and value.
 * Provides utility methods to retrieve questions, track which have been used,
 * and determine the current state of the board.
 * 
 * <p>This class is primarily used by {@link Game} to display available
 * categories, select questions, and check if the game has finished.</p>
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class GameBoard {

    /** Mapping of category names to a list of questions in that category. */
    private final Map<String, List<Question>> board = new LinkedHashMap<>();
    

    /**
     * Constructs a new {@code GameBoard} using a list of questions.
     * Questions are grouped by category and sorted by value in ascending order.
     *
     * @param questions the list of questions to populate the board
     * @throws IllegalArgumentException if the provided list is null or empty
     */
    public GameBoard(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("GameBoard requires at least one question");
        }

        // Group questions by category
        Map<String, List<Question>> grouped =
                questions.stream()
                         .collect(Collectors.groupingBy(
                                 Question::getCategory,
                                 LinkedHashMap::new,
                                 Collectors.toList()
                         ));

        // Sort each category’s questions by value
        grouped.forEach((cat, list) ->
                list.sort(Comparator.comparingInt(Question::getValue)));

        board.putAll(grouped);
    }


    /**
     * Returns a list of all category names on the game board.
     *
     * @return a list of category names in the order they were added
     */
    public List<String> getCategories() {
        return new ArrayList<>(board.keySet());
    }


    /**
     * Returns a list of available values in a given category
     * that have not yet been used in the game.
     *
     * @param category the category to query
     * @return a sorted list of available question values, or empty if none
     */    
    public List<Integer> getAvailableValues(String category) {
        List<Question> qList = board.get(category);
        if (qList == null) return Collections.emptyList();

        return qList.stream()
                .filter(q -> !q.isUsed())
                .map(Question::getValue)
                .sorted()
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a question from a given category with the specified value
     * that has not yet been used.
     *
     * @param category the category name
     * @param value    the point value of the question
     * @return the matching {@link Question} or {@code null} if none found
     */    
    public Question getQuestion(String category, int value) {
        List<Question> qList = board.get(category);
        if (qList == null) return null;

        return qList.stream()
                .filter(q -> q.getValue() == value && !q.isUsed())
                .findFirst()
                .orElse(null);
    }


    /**
     * Marks a question as used so it is no longer available for selection.
     *
     * @param question the {@link Question} to mark as used
     */    
    public void markUsed(Question question) {
        if (question != null) {
            question.setUsed(true);
        }
    }


    /**
     * Returns true if all questions on the board have been used.
     *
     * @return {@code true} if no questions remain, {@code false} otherwise
     */    
    public boolean isFinished() {
        return board.values().stream()
                .flatMap(List::stream)
                .allMatch(Question::isUsed);
    }


    /**
     * Counts the number of questions that have not yet been used.
     *
     * @return the number of remaining questions
     */    
    public long remainingCount() {
        return board.values().stream()
                .flatMap(List::stream)
                .filter(q -> !q.isUsed())
                .count();
    }
}
