package oop2.jeopardy;

import oop2.jeopardy.game.Game;
import oop2.jeopardy.model.Question;
import oop2.jeopardy.parser.QuestionParser;
import oop2.jeopardy.factory.ParserFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the OOP2 Multiplayer Jeopardy game.
 * <p>
 * This class provides a console-based interface that:
 * <ul>
 *     <li>Prompts the user to choose a question data file format (CSV, JSON, XML)</li>
 *     <li>Uses a {@link ParserFactory} to load the selected file format via a {@link QuestionParser}</li>
 *     <li>Loads all questions into memory and validates them</li>
 *     <li>Starts the {@link Game} with the loaded questions</li>
 * </ul>
 * <p>
 * This class handles input validation, error reporting for file loading, and
 * ensures that the game only starts if questions are successfully loaded.
 * <p>
 * Usage (via Maven):
 * <pre>
 * mvn compile exec:java -Dexec.mainClass=oop2.jeopardy.Main
 * </pre>
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class Main {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Main() {
        // Prevent instantiation
    }
    /**
     * Main method. Prompts the user for a data file format, loads questions,
     * and starts the game.
     * <p>
     * Steps:
     * <ol>
     *     <li>Prompt the user to select CSV, JSON, or XML input</li>
     *     <li>Use {@link ParserFactory#getParser(String)} to obtain the appropriate parser</li>
     *     <li>Load and validate questions from the selected file</li>
     *     <li>Create a {@link Game} instance and start it</li>
     * </ol>
     * <p>
     * If an invalid input is provided, defaults to JSON format. Any errors
     * during file loading or game execution are printed to the console and
     * terminate the program if necessary.
     *
     * @param args command-line arguments (ignored)
     */
    @SuppressWarnings("resource")
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== OOP2 MULTIPLAYER JEOPARDY ===");
        System.out.println("Choose data file format:");
        System.out.println("1) CSV");
        System.out.println("2) JSON");
        System.out.println("3) XML");
        System.out.print("> ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input. Defaulting to JSON.");
            choice = 2;
        }

        String extension = switch (choice) {
            case 1 -> "csv";
            case 2 -> "json";
            case 3 -> "xml";
            default -> "json";
        };

        String path = switch (choice) {
            case 1 -> "data/sample_game_CSV.csv";
            case 2 -> "data/sample_game_JSON.json";
            case 3 -> "data/sample_game_XML.xml";
            default -> "data/sample_game_JSON.json";
        };

        List<Question> questions = null;

        try {
            System.out.println("Loading " + extension.toUpperCase() + "...");

            // Use Factory to create parser
            QuestionParser parser = ParserFactory.getParser(extension);

            // Load questions
            questions = parser.load(path);

        } catch (Exception ex) {
            System.out.println("ERROR loading questions: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        
        System.out.println("\nLoaded " + (questions != null ? questions.size() : 0) + " questions.");
        System.out.println("Starting game...\n");

        try {
            Game game = new Game(questions);
            game.start();
        } catch (Exception e) {
            System.out.println("Unexpected error running the game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
