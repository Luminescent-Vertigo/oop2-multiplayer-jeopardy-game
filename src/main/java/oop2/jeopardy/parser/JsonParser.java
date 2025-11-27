package oop2.jeopardy.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import oop2.jeopardy.model.Question;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Parses Jeopardy questions from a JSON file located in the application's resources.
 * <p>
 * This class uses Jackson's {@link ObjectMapper} to deserialize a JSON array of
 * question objects directly into {@link Question} instances. After loading, each
 * question is validated to ensure consistency and correctness in the dataset.
 * <p>
 * The JSON file must contain an array of objects with fields matching those in
 * {@link Question}, including: Category, Question, Value, Options (A–D), and
 * CorrectAnswer.
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class JsonParser implements QuestionParser {

    /**
     * Creates a new JsonParser instance.
     */
    public JsonParser() {
        // default constructor
    }

    /**
     * Loads and parses a JSON resource file containing Jeopardy questions.
     *
     * <p>The method reads the file as a stream from the classpath, converts it to a
     * typed list of {@link Question} objects using Jackson, and then validates each
     * entry to ensure all required properties are present and valid.</p>
     *
     * @param filename the name of the JSON file located in the resources folder
     * @return a list of parsed and validated {@link Question} objects
     *
     * @throws FileNotFoundException if the JSON file cannot be located as a resource
     * @throws IllegalArgumentException if any question fails validation checks
     * @throws Exception if Jackson encounters parsing/format issues or I/O failure
     */
    @Override
    public List<Question> load(String filename) throws Exception {

        InputStream in = getClass().getClassLoader().getResourceAsStream(filename);

        if (in == null) {
            throw new FileNotFoundException("JSON resource not found: " + filename);
        }

        ObjectMapper mapper = new ObjectMapper();

        List<Question> questions = mapper.readValue(
                in,
                mapper.getTypeFactory().constructCollectionType(List.class, Question.class)
        );

        // Validate each question
        for (Question q : questions) {
            validateQuestion(q);
        }

        return questions;
    }


    /**
     * Validates a single {@link Question} instance to ensure it contains
     * all required data and follows Jeopardy formatting rules.
     *
     * <p>Validation includes checking the presence of:</p>
     * <ul>
     *     <li>Non-empty category</li>
     *     <li>Non-empty question text</li>
     *     <li>Positive monetary value</li>
     *     <li>An options map with keys A–D</li>
     *     <li>Non-blank option text</li>
     *     <li>A correct answer that is exactly "A", "B", "C", or "D"</li>
     * </ul>
     *
     * @param q the question to validate
     * @throws IllegalArgumentException if any validation rule fails
     */
    private void validateQuestion(Question q) {

        if (q == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        // Validate category
        if (q.getCategory() == null || q.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }

        // Validate question text
        if (q.getQuestion() == null || q.getQuestion().isBlank()) {
            throw new IllegalArgumentException("Question text cannot be null or blank");
        }

        // Validate value
        if (q.getValue() <= 0) {
            throw new IllegalArgumentException("Value must be a positive integer");
        }

        // Validate options
        Map<String, String> opts = q.getOptions();
        if (opts == null) {
            throw new IllegalArgumentException("Options map cannot be null");
        }

        if (opts.size() != 4) {
            throw new IllegalArgumentException("Options must contain exactly 4 entries (A, B, C, D)");
        }

        if (!opts.containsKey("A") ||
            !opts.containsKey("B") ||
            !opts.containsKey("C") ||
            !opts.containsKey("D")) {
            throw new IllegalArgumentException("Options must include keys A, B, C, D");
        }

        // Validate that option values are not null/blank
        for (String key : List.of("A", "B", "C", "D")) {
            String text = opts.get(key);
            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException("Option " + key + " cannot be null or blank");
            }
        }

        // Validate correct answer
        String correct = q.getCorrectAnswer();
        if (correct == null || correct.isBlank()) {
            throw new IllegalArgumentException("Correct answer cannot be null or blank");
        }

        if (!"ABCD".contains(correct)) {
            throw new IllegalArgumentException("Correct answer must be one of A, B, C, or D");
        }
    }
}
