package oop2.jeopardy.factory;

import oop2.jeopardy.parser.*;

/**
 * Factory class responsible for creating the appropriate {@link QuestionParser}
 * implementation based on a provided file extension. Supports JSON, CSV, and XML
 * parser creation.
 *
 * <p>This class follows the Factory Design Pattern to encapsulate parser
 * instantiation logic and to simplify parser selection throughout the
 * application.</p>
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class ParserFactory {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * <p>
     * This ensures that all parser creation is handled exclusively through the
     * static {@link #getParser(String)} method.
     */
    private ParserFactory() {
        // No-op: utility class
    }

    /**
     * Returns a concrete {@link QuestionParser} instance that matches the provided
     * file extension. The extension is normalized to lowercase before matching.
     *
     * @param extension the file extension (e.g., "json", "csv", "xml") without the dot
     * @return the corresponding {@link QuestionParser} implementation
     * @throws IllegalArgumentException if the format is unsupported
     */

    public static QuestionParser getParser(String extension) {

        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("Extension cannot be null or empty.");
        }

        extension = extension.toLowerCase();

        return switch (extension) {
            case "json" -> new JsonParser();
            case "csv"  -> new CsvParser();
            case "xml"  -> new XmlParser();
            default -> throw new IllegalArgumentException("Unsupported format: " + extension);
        };
    }
}
