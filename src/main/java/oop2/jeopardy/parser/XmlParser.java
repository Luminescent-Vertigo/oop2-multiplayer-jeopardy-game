package oop2.jeopardy.parser;

import oop2.jeopardy.model.Question;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Parses Jeopardy-style questions from an XML resource file.
 * <p>
 * This parser expects the XML to contain a series of {@code <QuestionItem>} nodes,
 * each containing: Category, QuestionText, Value, Options (OptionA–OptionD), and
 * CorrectAnswer. Each parsed {@link Question} is validated to ensure correct
 * formatting and semantic validity.
 * <p>
 * This class uses the standard Java DOM API via {@link DocumentBuilderFactory}
 * to process the XML structure.
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class XmlParser implements QuestionParser {

    /**
     * Creates a new XmlParser instance.
     */
    public XmlParser() {
        // default constructor
    }

    /**
     * Loads and parses an XML resource file into a list of {@link Question} objects.
     *
     * <p>The XML file is expected to be located on the classpath. It is parsed
     * using DOM, and each {@code <QuestionItem>} node is converted into a fully
     * populated {@link Question} instance. Each question undergoes structural
     * and semantic validation before being added to the output list.</p>
     *
     * @param filename the XML file name within the application's resources
     * @return a list of validated {@link Question} objects
     *
     * @throws FileNotFoundException if the XML file cannot be found as a resource
     * @throws IllegalArgumentException if the XML contains malformed questions,
     *                                  invalid values, missing fields, or no questions at all
     * @throws Exception for low-level parsing errors (DOM parsing failures, I/O issues)
     */
    @Override
    public List<Question> load(String filename) throws Exception {

        InputStream in = getClass().getClassLoader().getResourceAsStream(filename);

        if (in == null) {
            throw new FileNotFoundException("XML resource not found: " + filename);
        }

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(in);

        doc.getDocumentElement().normalize();

        List<Question> questions = new ArrayList<>();

        NodeList nodes = doc.getElementsByTagName("QuestionItem");

        for (int i = 0; i < nodes.getLength(); i++) {

            Node n = nodes.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;

            Element e = (Element) n;

            // Read <Options> block
            Map<String, String> opts = new LinkedHashMap<>();
            Element optNode = (Element) e.getElementsByTagName("Options").item(0);

            opts.put("A", text(optNode, "OptionA"));
            opts.put("B", text(optNode, "OptionB"));
            opts.put("C", text(optNode, "OptionC"));
            opts.put("D", text(optNode, "OptionD"));

            // Parse question value
            int value;
            try {
                value = Integer.parseInt(text(e, "Value").trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Value must be a valid integer");
            }

            // Build final Question
            Question q = new Question(
                    text(e, "Category"),
                    value,
                    text(e, "QuestionText"),
                    opts,
                    text(e, "CorrectAnswer")
            );

            validateQuestion(q);
            questions.add(q);
        }

        // If XML structure is valid but contains no questions, treat as error
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("XML contains no questions");
        }

        return questions;
    }


    /**
     * Retrieves the text content of a specific child tag within a parent element.
     * <p>
     * If the tag does not exist, an empty string is returned instead of null.
     *
     * @param parent the parent XML element
     * @param tag    the name of the child tag whose text should be extracted
     * @return trimmed text content of the tag, or an empty string if missing
     */
    private String text(Element parent, String tag) {
        if (parent == null) return "";
        NodeList n = parent.getElementsByTagName(tag);
        return n.getLength() == 0 ? "" : n.item(0).getTextContent().trim();
    }


    /**
     * Validates that the given {@link Question} object is well-formed and
     * semantically valid.
     * <p>
     * All questions must satisfy:
     * <ul>
     *     <li>Non-null, non-blank category</li>
     *     <li>Non-null, non-blank question text</li>
     *     <li>Positive integer value</li>
     *     <li>Options map containing exactly A–D</li>
     *     <li>Each option must be non-null and non-blank</li>
     *     <li>Correct answer must be one of A, B, C, or D</li>
     * </ul>
     *
     * @param q the question to validate
     * @throws IllegalArgumentException if the question violates any expected rule
     */
    private void validateQuestion(Question q) {
        if (q == null) throw new IllegalArgumentException("Question cannot be null");

        // Category
        if (q.getCategory() == null || q.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }

        // Question text
        if (q.getQuestion() == null || q.getQuestion().isBlank()) {
            throw new IllegalArgumentException("Question text cannot be null or blank");
        }

        // Value
        if (q.getValue() <= 0) {
            throw new IllegalArgumentException("Value must be a positive integer");
        }

        // Options
        Map<String, String> opts = q.getOptions();
        if (opts == null) throw new IllegalArgumentException("Options map cannot be null");

        if (opts.size() != 4)
            throw new IllegalArgumentException("Options must contain exactly 4 entries (A, B, C, D)");

        for (String key : List.of("A", "B", "C", "D")) {
            if (!opts.containsKey(key)) throw new IllegalArgumentException("Options must include keys A, B, C, D");

            String val = opts.get(key);
            if (val == null || val.isBlank()) throw new IllegalArgumentException("Option " + key + " cannot be null or blank");
        }

        // Correct answer
        String correct = q.getCorrectAnswer();
        if (correct == null || correct.isBlank())
            throw new IllegalArgumentException("Correct answer cannot be null or blank");

        if (!"ABCD".contains(correct))
            throw new IllegalArgumentException("Correct answer must be one of A, B, C, or D");
    }
}
