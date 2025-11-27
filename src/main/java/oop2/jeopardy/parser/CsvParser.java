package oop2.jeopardy.parser;

import oop2.jeopardy.model.Question;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Parses Jeopardy questions from a CSV file stored in the application's resources.
 * <p>
 * This parser uses Apache Commons CSV to read rows where each represents a
 * Jeopardy-style question with a category, monetary value, question text,
 * four answer options (A–D), and a correct answer key.
 * <p>
 * The parser performs trimming, validation, and throws informative
 * exceptions for malformed input. It returns a fully populated list of
 * {@link Question} objects ready for use in the game.
 *
 * <p><strong>Expected CSV Columns:</strong></p>
 * <ul>
 *     <li>Category</li>
 *     <li>Value</li>
 *     <li>Question</li>
 *     <li>OptionA</li>
 *     <li>OptionB</li>
 *     <li>OptionC</li>
 *     <li>OptionD</li>
 *     <li>CorrectAnswer (A/B/C/D)</li>
 * </ul>
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class CsvParser implements QuestionParser {

    /**
     * Creates a new CsvParser instance.
     */
    public CsvParser() {
        // default constructor
    }


    /**
     * Loads and parses a CSV file containing Jeopardy question data.
     *
     * <p>The CSV file must exist inside the application's resource directory
     * (i.e., accessible through the classpath). Header names must match the
     * expected column names exactly, though whitespace is trimmed for values.</p>
     *
     * @param filename the name of the CSV file located in the resources folder
     * @return a list of parsed {@link Question} objects
     *
     * @throws NullPointerException     if {@code filename} is null
     * @throws FileNotFoundException    if the CSV file cannot be found as a resource
     * @throws NumberFormatException    if the "Value" field is not a valid integer
     * @throws IllegalArgumentException if "CorrectAnswer" is not A, B, C, or D
     * @throws Exception                if parsing fails for any other reason
     */
    @Override
    public List<Question> load(String filename) throws Exception {

        if (filename == null)
            throw new NullPointerException("Filename cannot be null.");

        InputStream in = getClass().getClassLoader().getResourceAsStream(filename);

        if (in == null) {
            throw new FileNotFoundException("CSV resource not found: " + filename);
        }

        InputStreamReader reader = new InputStreamReader(in);

        CSVParser parser = CSVParser.parse(
                reader,
                CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
        );

        List<Question> questions = new ArrayList<>();

        for (CSVRecord r : parser) {

            // Trimmed fields to avoid whitespace-related failures
            String category = r.get("Category").trim();
            String questionText = r.get("Question").trim();
            String valueText = r.get("Value").trim();
            String correct = r.get("CorrectAnswer").trim();

            int value = Integer.parseInt(valueText); // may throw NumberFormatException (good for our tests)

            // Build options map (each trimmed)
            Map<String, String> opts = new LinkedHashMap<>();
            opts.put("A", r.get("OptionA").trim());
            opts.put("B", r.get("OptionB").trim());
            opts.put("C", r.get("OptionC").trim());
            opts.put("D", r.get("OptionD").trim());

            // Validate correct answer is one of A/B/C/D
            if (!List.of("A", "B", "C", "D").contains(correct)) {
                throw new IllegalArgumentException(
                        "CorrectAnswer must be one of A, B, C, or D, but got: " + correct
                );
            }

            // Construct Question object and add to list
            /*questions.add(new Question(
                    category,
                    value,
                    questionText,
                    opts,
                    correct
            ));*/
            Question q = new Question(
                    category,
                    value,
                    questionText,
                    opts,
                    correct
            );

            // Perform FULL VALIDATION (same as JsonParser)
            validateQuestion(q);

            questions.add(q);
        }

        return questions;
    }

    /**
     * Validates a {@link Question} instance to ensure it contains all required
     * Jeopardy question data and follows the same strict rules used by
     * {@link JsonParser}.
     *
     * <p>This method enforces the following validation constraints:</p>
     * <ul>
     *     <li>The {@link Question} object itself must not be {@code null}</li>
     *     <li>Category text must be non-null and non-blank</li>
     *     <li>Question text must be non-null and non-blank</li>
     *     <li>Value must be a strictly positive integer</li>
     *     <li>The options map must:
     *         <ul>
     *             <li>Exist (not {@code null})</li>
     *             <li>Contain exactly four entries</li>
     *             <li>Include keys "A", "B", "C", and "D"</li>
     *             <li>Provide non-null, non-blank text for each option</li>
     *         </ul>
     *     </li>
     *     <li>The correct answer must be non-null, non-blank, and
     *         exactly one of {@code "A"}, {@code "B"}, {@code "C"}, or {@code "D"}</li>
     * </ul>
     *
     * <p>If any rule is violated, this method throws an
     * {@link IllegalArgumentException} describing the specific failure.</p>
     *
     * @param q the {@link Question} object to validate
     * @throws IllegalArgumentException if any validation rule fails or the question data is malformed
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

        // Validate non-blank options
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