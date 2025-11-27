package oop2.jeopardy.model;

import java.util.List;

/**
 * Represents a Jeopardy question category, containing a list of
 * {@link Question} objects belonging to that category.
 * <p>
 * Each category has:
 * <ul>
 *     <li>A display name (e.g., "Science")</li>
 *     <li>A list of questions associated with that category</li>
 * </ul>
 *
 * This class is a simple data model used by the game engine, parsers,
 * and board construction logic.
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class Category {

    /** Name of the category. */
    private String name;

    /** List of questions belonging to this category. */
    private List<Question> questions;


    /**
     * Creates a new category containing a name and associated questions.
     *
     * @param name       the name of the category (must not be null or blank)
     * @param questions  the list of questions belonging to this category
     * @throws IllegalArgumentException if the category name is null or blank
     */
    public Category(String name, List<Question> questions) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be null or blank");
        }
        this.name = name.trim();
        this.questions = questions;
    }


    /**
     * Returns the name of this category.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the list of questions that belong to this category.
     *
     * @return list of {@link Question} objects
     */
    public List<Question> getQuestions() {
        return questions;
    }


    /**
     * Returns the category name as its string representation.
     *
     * @return the name of the category
     */
    @Override
    public String toString() {
        return name;
    }
}
