package oop2.jeopardy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents a single Jeopardy-style question containing:
 * <ul>
 *     <li>A category name</li>
 *     <li>A point value</li>
 *     <li>The question text</li>
 *     <li>A map of answer options, typically A–D</li>
 *     <li>The correct answer key (e.g., "A")</li>
 *     <li>A flag indicating whether the question has been used</li>
 * </ul>
 *
 * <p>This class is mapped from JSON files using Jackson. The
 * {@link JsonIgnoreProperties} annotation ensures that additional or unknown
 * fields in the JSON source are safely ignored during parsing.
 *
 * <p>Used by parsers, the {@link oop2.jeopardy.game.QuestionBank}, and the
 * {@link oop2.jeopardy.game.GameBoard} to display questions, validate
 * answers, and track question usage.
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)  // ← This fixes UnrecognizedPropertyException
public class Question {

     /** Whether the question has already been answered/used. */
    private boolean used = false;


    /** Category to which the question belongs (JSON: "Category"). */
    @JsonProperty("Category")
    private String category;


    /** Point value of the question (JSON: "Value"). */
    @JsonProperty("Value")
    private int value;


    /** The full text of the question (JSON: "Question"). */
    @JsonProperty("Question")
    private String question;


    /** Map of available answer options, typically labeled A–D (JSON: "Options"). */
    @JsonProperty("Options")
    private Map<String, String> options;


    /** The correct answer key (e.g., "A") (JSON: "CorrectAnswer"). */
    @JsonProperty("CorrectAnswer")
    private String correctAnswer;


    /**
     * Default constructor required for JSON deserialization.
     * <p>Jackson creates an empty instance and populates fields using setters or reflection.
     */
    public Question() {}


    /**
     * Constructs a fully initialized {@code Question}.
     *
     * @param category       the category of the question
     * @param value          the assigned point value
     * @param question       the text of the question
     * @param options        map of answer choices (e.g., A–D)
     * @param correctAnswer  the key representing the correct answer
     */
    public Question(String category, int value, String question, Map<String, String> options, String correctAnswer) {
        this.category = category;
        this.value = value;
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

     /** Get the category name
      * 
      * @return the category name */
    public String getCategory() { return category; }

    /** Get the point value of the question
     * 
     * @return the point value of the question */
    public int getValue() { return value; }

    /** Get the question text
     * 
     * @return the question text */
    public String getQuestion() { return question; }

    /** Get a map of answer options
     * 
     * @return a map of answer options */
    public Map<String, String> getOptions() { return options; }

    /** Get the correct answer key (e.g., "A")
     * 
     * @return the correct answer key (e.g., "A") */
    public String getCorrectAnswer() { return correctAnswer; }

    /** Get whether the question has been used
     * 
     * @return whether the question has been used */
    public boolean isUsed() { return used; }

    /**
     * Marks the question as used/un-used.
     *
     * @param used true if the question is now used
     */
    public void setUsed(boolean used) { this.used = used; }
}
