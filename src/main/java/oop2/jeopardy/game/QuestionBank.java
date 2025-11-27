package oop2.jeopardy.game;

import oop2.jeopardy.model.Question;

import java.util.*;

/**
 * Stores and organizes all questions for the game.
 * Provides quick access to questions by category and value,
 * as well as utility methods for retrieving lists and counts.
 * 
 * <p>Used by {@link GameBoard} and {@link Game} to fetch questions
 * and populate the game board.</p>
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class QuestionBank {

    /** Mapping: category → (value → question) */
    private final Map<String, Map<Integer, Question>> questions = new HashMap<>();


    /**
     * Constructs a new {@code QuestionBank} with a list of questions.
     *
     * @param questionList the list of questions to store
     */
    public QuestionBank(List<Question> questionList) {
        load(questionList);
    }
  

    /**
     * Loads the questions into the internal data structure.
     *
     * @param questionList the list of questions to load
     */
    private void load(List<Question> questionList) {
        for (Question q : questionList) {
            String category = q.getCategory();
            int value = q.getValue();

            questions
                .computeIfAbsent(category, c -> new HashMap<>())
                .put(value, q);
        }
    }


    /**
     * Retrieves a question by category and value.
     *
     * @param category the category name
     * @param value the point value
     * @return the corresponding {@link Question}, or {@code null} if not found
     */
    public Question get(String category, int value) {
        Map<Integer, Question> categoryMap = questions.get(category);
        if (categoryMap == null) return null;
        return categoryMap.get(value);
    }


    /**
     * Returns all categories stored in this bank.
     *
     * @return a set of category names
     */
    public Set<String> getCategories() {
        return questions.keySet();
    }


    /**
     * Returns all available point values for a given category.
     *
     * @param category the category name
     * @return a set of integer values, or empty set if category not found
     */
    public Set<Integer> getValuesInCategory(String category) {
        Map<Integer, Question> categoryMap = questions.get(category);
        return categoryMap == null ? Set.of() : categoryMap.keySet();
    }


    /**
     * Checks if a question exists for a given category and value.
     *
     * @param category the category name
     * @param value the point value
     * @return {@code true} if a question exists, {@code false} otherwise
     */
    public boolean hasQuestion(String category, int value) {
        return get(category, value) != null;
    }


    /**
     * Returns the total number of questions in the bank.
     *
     * @return total number of questions
     */
    public int size() {
        return questions.values().stream()
                .mapToInt(Map::size)
                .sum();
    }

    /**
     * Returns a list of all questions in the bank.
     *
     * @return list of all {@link Question} objects
     */
    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        for (Map<Integer, Question> map : questions.values()) {
            list.addAll(map.values());
        }
        return list;
    }

}

