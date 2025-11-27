package oop2.jeopardy.parser;

import oop2.jeopardy.model.Question;

import java.util.List;

/**
 * Interface for parsing question files into {@link Question} objects.
 * <p>
 * Implementations may support different file formats (CSV, JSON, XML, etc.).
 * Each parser is responsible for loading the file and returning a validated
 * list of {@link Question} objects.
 *
 * <p>Implementations must throw an exception if:
 * <ul>
 *     <li>The file cannot be found</li>
 *     <li>The file is malformed</li>
 *     <li>Any questions are invalid according to the format rules</li>
 * </ul>
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public interface QuestionParser {

    /**
     * Loads questions from a file and returns them as a list of {@link Question} objects.
     *
     * @param filePath the path (or resource name) of the file containing questions
     * @return a list of validated {@link Question} objects
     * @throws Exception if the file cannot be read, parsed, or contains invalid questions
     */
    public List<Question> load(String filePath) throws Exception;
}
