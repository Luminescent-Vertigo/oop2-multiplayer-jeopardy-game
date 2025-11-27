package oop2.jeopardy.game;

import oop2.jeopardy.logging.GameEventLogger;
import oop2.jeopardy.model.Question;
import oop2.jeopardy.report.DocxReportGenerator;
import oop2.jeopardy.report.PdfReportGenerator;
import oop2.jeopardy.report.ReportGenerator;
import oop2.jeopardy.report.TxtReportGenerator;

import java.io.File;
import java.util.*;

/**
 * Represents the main controller for running a multiplayer Jeopardy game.
 * This class manages player setup, the game loop, question flow, score handling,
 * event logging, report generation, and end-of-game statistics.
 *
 * <p>The {@code Game} class acts as the core orchestrator that interacts with
 * {@link GameBoard}, {@link TurnManager}, {@link QuestionBank}, and
 * {@link GameEventLogger} to produce a complete game experience.</p>
 *
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */
public class Game {

    /** Scanner used for reading console input from players. */
    private final Scanner scanner = new Scanner(System.in);

    /** Bank of all questions used throughout the game. */
    private QuestionBank questionBank;

    /** List of all participating players. */
    private List<Player> players;

    /** Manages player turn order. */
    private TurnManager turnManager;

    /** Represents the game board containing categories and values. */
    private GameBoard gameBoard;

    /** Stores a full history of all actions for reporting. */
    private GameHistory gameHistory = new GameHistory();

    /** Responsible for logging all game events to file. */
    private GameEventLogger eventLogger;
    

    /**
     * Creates a new Game instance using a list of questions.
     *
     * @param questions a list of pre-loaded questions for the session
     */
    public Game(List<Question> questions) {
        this.questionBank = new QuestionBank(questions);
        this.eventLogger = new GameEventLogger(); // Logger automatically generates GAME001, GAME002, etc.
    }

    /*============================================================
     *  GAME START
     *============================================================*/

    /**
     * Starts the Jeopardy game. This includes initializing the game board,
     * setting up players, running the main game loop, displaying final scores,
     * and generating an optional report.
     *
     * @throws Exception if a report fails to generate
     */
    public void start() throws Exception {
        System.out.println("\n=== WELCOME TO OOP2 MULTIPLAYER JEOPARDY ===\n");

        eventLogger.log(
            "SYSTEM",
            "Start Game",
            "",
            "",
            "",
            "",
            "0"
        );


        // 1) Load questions
        gameBoard = new GameBoard(questionBank.getAllQuestions());
        eventLogger.log(
            "SYSTEM",
            "Load File",
            "",
            "",
            "",
            "",
            "0"
        );

        eventLogger.log(
            "SYSTEM",
            "File Loaded Successfully",
            "",
            "",
            "",
            "Success",
            "0"
        );


        // 2) Setup players
        setupPlayers();
        turnManager = new TurnManager(players);

        System.out.println("\n--- GAME START ---\n");


        // 3) Main loop
        gameLoop();

        // 4) Final summary
        showFinalScores();

        // 5) Generate report
        generateReport();

        eventLogger.log(
            "SYSTEM",
            "Exit Game",
            "",
            "",
            "",
            "OK",
            "0"
        );

        System.out.println("\nThanks for playing!\n");
    }


    /*============================================================
     *  PLAYER SETUP
     *============================================================*/

    /**
     * Prompts the user to input how many players will join,
     * and collects each player's name. Player creation events
     * are logged to the event logger.
     */
    private void setupPlayers() {
        System.out.print("How many players? (1–4): ");
        int count = readIntInRange(1, 4);

        eventLogger.log(
            "SYSTEM",
            "Select Player Count",
            "",
            "",
            String.valueOf(count),
            "OK",
            "0"
        );


        players = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            System.out.print("Enter name for player #" + i + ": ");
            String name = scanner.nextLine().trim();
            eventLogger.log(
                name,
                "Enter Player Name",
                "",
                "",
                name,
                "OK",
                "0"
            );

            players.add(new Player(name));
        }
    }


    /*============================================================
     *  GAME LOOP
     *============================================================*/

    /**
     * The primary game loop. Each iteration represents one player's turn.
     * The loop runs until all questions are exhausted or a player quits.
     */
    private void gameLoop() {
        while (!gameBoard.isFinished()) {
            Player current = turnManager.getCurrentPlayer();
            eventLogger.log(
                current.getName(),
                "Start Turn",
                "",
                "",
                "",
                "",
                String.valueOf(current.getScore())
            );

            System.out.println("\n------------------------------------------");
            System.out.println("Turn: " + current.getName());
            System.out.println("------------------------------------------");

            showBoard();

            // Allow quitting
            System.out.print("\nEnter category or Q to quit: ");
            String category = scanner.nextLine().trim();

            if (category.equalsIgnoreCase("Q")) {
                System.out.println("\nPlayer quit. Ending game...");

                eventLogger.log(
                    current.getName(),
                    "Exit Game",
                    "",
                    "",
                    "",
                    "",
                    String.valueOf(current.getScore())
                );

                break;
            }

            // Validate category
            if (!gameBoard.getCategories().contains(category)) {
                System.out.println("Invalid category, try again.");

                eventLogger.log(
                    current.getName(),
                    "Select Category",
                    category,
                    "",
                    "",
                    "Invalid",
                    String.valueOf(current.getScore())
                );

                continue;
            }

            List<Integer> values = gameBoard.getAvailableValues(category);
            if (values.isEmpty()) {
                System.out.println("No values left in that category!");
                continue;
            }

            eventLogger.log(
                current.getName(),
                "Select Category",
                category,
                "",
                "",
                "OK",
                String.valueOf(current.getScore())
            );


            System.out.println("Available values: " + values);
            System.out.print("Enter value: ");

            int value;
            try {
                value = Integer.parseInt(scanner.nextLine());

                eventLogger.log(
                    current.getName(),
                    "Select Question",
                    category,
                    String.valueOf(value),
                    "",
                    "OK",
                    String.valueOf(current.getScore())
                );

            } catch (NumberFormatException ex) {
                System.out.println("Invalid number.");
                continue;
            }

            if (!values.contains(value)) {
                System.out.println("Invalid value.");

                eventLogger.log(
                    current.getName(),
                    "Select Question",
                    category,
                    String.valueOf(value),
                    "",
                    "Invalid",
                    String.valueOf(current.getScore())
                );

                continue;
            }

            Question q = gameBoard.getQuestion(category, value);
            if (q == null) {
                System.out.println("That question was already used!");
                continue;
            }

            askQuestion(current, q);

            // Next player
            turnManager.next();
        }
    }


    /*============================================================
     *  SHOW BOARD
     *============================================================*/

    /**
     * Displays the current state of the game board and logs the action.
     */
    private void showBoard() {
        System.out.println("\n=== Categories ===");
        eventLogger.log(
            "SYSTEM",
            "Show Board",
            "",
            "",
            "",
            "OK",
            "0"
        );

        for (String cat : gameBoard.getCategories()) {
            System.out.printf("- %-25s  %s%n", cat, gameBoard.getAvailableValues(cat));
        }
    }


    /*============================================================
     *  ASK QUESTION
     *============================================================*/

    /**
     * Asks a question to the current player, processes the answer,
     * updates the score, logs the event, and stores the game record.
     *
     * @param player the player currently answering
     * @param q      the question being asked
     */
    private void askQuestion(Player player, Question q) {
        System.out.printf("\n[%s for %d pts]\n", q.getCategory(), q.getValue());
        System.out.println(q.getQuestion());

        Map<String, String> opts = q.getOptions();
        
        for (var entry : opts.entrySet()) {
            System.out.printf(" %s) %s%n", entry.getKey(), entry.getValue());
        }

        System.out.print("\nYour answer (A–D): ");
        String ans = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

        String fullAnswer = opts.get(ans); // full answer

        boolean correct = ans.equals(q.getCorrectAnswer()); // correct?
        int earned = correct ? q.getValue() : -q.getValue();
        player.addScore(earned);

        eventLogger.log(
            player.getName(),
            "Score Updated",
            q.getCategory(),
            String.valueOf(q.getValue()),
            ans,
            correct ? "Correct" : "Incorrect",
            String.valueOf(player.getScore())
        );


        System.out.println(correct ? "✅ Correct!" : "❌ Incorrect!");
        System.out.println("Correct answer: " + q.getCorrectAnswer());
        System.out.println("Points earned: " + earned);
        System.out.println("Running score: " + player.getScore());

        // Mark used
        gameBoard.markUsed(q);

        // Log history for in-memory record and report
        gameHistory.add(new GameRecord(
                player.getName(),
                q.getCategory(),
                q.getValue(),
                q.getQuestion(),
                ans,
                q.getCorrectAnswer(),
                correct,
                earned,
                player.getScore()
        ));

        // Log to CSV file
        eventLogger.log(
                player.getName(),
                "Answered Question",
                q.getCategory(),
                String.valueOf(q.getValue()),
                ans + ") " + fullAnswer,
                correct ? "Correct" : "Incorrect",
                String.valueOf(player.getScore())
        );

    }


    /*============================================================
     *  GENERATE REPORT
     *============================================================*/

    /**
     * Interacts with the user to determine whether a report should be generated.
     * Allows choosing from TXT, PDF, or DOCX output formats and handles filename
     * creation and file output.
     */
    private void generateReport() {

        System.out.print("\nGenerate report? (Y/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("Report cancelled.");
            eventLogger.log(
                "SYSTEM",
                "Generate Report",
                "",
                "",
                "",
                "Cancelled",
                "0"
            );

            return;
        }

        // -------------------------------
        // Choose format
        // -------------------------------
        System.out.println("\nChoose format:");
        System.out.println("1) TXT");
        System.out.println("2) PDF");
        System.out.println("3) DOCX");
        System.out.println("0) Cancel");
        System.out.print("> ");

        int choice = readIntInRange(0, 3);

        if (choice == 0) {
            System.out.println("Report cancelled.");
            return;
        }

        eventLogger.log(
            "SYSTEM",
            "Select Report Format",
            "",
            "",
            String.valueOf(choice),
            "OK",
            "0"
        );


        ReportGenerator generator = switch (choice) {
            case 1 -> new TxtReportGenerator();
            case 2 -> new PdfReportGenerator();
            case 3 -> new DocxReportGenerator();
            default -> new TxtReportGenerator();
        };

        eventLogger.log(
            "SYSTEM",
            "Generate Report",
            "",
            "",
            "",
            "OK",
            "0"
        );


        // -------------------------------
        // Ask for optional custom name
        // -------------------------------
        System.out.print("\nEnter report name (leave empty for default): ");
        String userFile = scanner.nextLine().trim();

        // sanitize basic invalid filename chars
        userFile = userFile.replaceAll("[\\\\/:*?\"<>|]", "").trim();

        // -------------------------------
        // Folder creation
        // -------------------------------
        String outputFolder = "reports";
        File folder = new File(outputFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        // -------------------------------
        // Filename selection
        // -------------------------------
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String baseName = userFile.isEmpty()
                ? "jeopardy_report_" + timestamp
                : userFile;

        String filename = switch (choice) {
            case 1 -> outputFolder + "/" + baseName + ".txt";
            case 2 -> outputFolder + "/" + baseName + ".pdf";
            case 3 -> outputFolder + "/" + baseName + ".docx";
            default -> outputFolder + "/" + baseName + ".txt";
        };

        // -------------------------------
        // Report generation
        // -------------------------------
        try {
            generator.generate(gameHistory, filename);

            System.out.println("\n=======================================");
            System.out.println(" Report created successfully!");
            System.out.println(" Location: " + filename);
            System.out.println("=======================================\n");

        } catch (Exception ex) {
            System.out.println("\nERROR creating report:");
            System.out.println(ex.getMessage());
            System.out.println();
            return;
        }

        // -------------------------------
        // Ask to open report automatically
        // -------------------------------
        System.out.print("Open the report now? (Y/N): ");
        String openChoice = scanner.nextLine().trim();

        if (openChoice.equalsIgnoreCase("Y")) {
            try {
                java.awt.Desktop.getDesktop().open(new File(filename));
            } catch (Exception e) {
                System.out.println("Unable to open automatically: " + e.getMessage());
            }
        }
    }


    /*============================================================
     *  FINAL OUTPUT
     *============================================================*/

    /**
     * Displays the final scoreboard, determines the winner, and prints
     * additional statistics including highest correct answers and highest
     * incorrect answers.
     */
    private void showFinalScores() {

        System.out.println("\n================ FINAL SCOREBOARD ================");

        // Display sorted scoreboard (highest first)
        players.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .forEach(p -> {
                    String name = String.format("%-15s", p.getName());
                    String score = String.format("%6d", p.getScore());
                    System.out.printf(" %s : %s pts\n", name, score);
                });

        System.out.println("==================================================");
        System.out.println("Turns played: " + gameHistory.size());

        /* -----------------------------
           Winner Announcement
           ----------------------------- */
        Player winner = players.stream()
                .max(Comparator.comparingInt(Player::getScore))
                .orElse(null);

        if (winner != null) {
            System.out.println("\n🏆 WINNER: " + winner.getName() +
                    " with " + winner.getScore() + " points! 🏆");
        }

        /* -----------------------------
           Statistics: Correct / Incorrect
           ----------------------------- */
        Map<String, Integer> correctCounts = gameHistory.getCorrectCounts();
        Map<String, Integer> incorrectCounts = gameHistory.getIncorrectCounts();

        // Most correct
        Map.Entry<String, Integer> best = correctCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (best != null) {
            System.out.println("\n⭐ MOST CORRECT ANSWERS: " +
                    best.getKey() + " (" + best.getValue() + ")");
        }

        // Most incorrect
        Map.Entry<String, Integer> worst = incorrectCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (worst != null) {
            System.out.println("❌ MOST INCORRECT ANSWERS: " +
                    worst.getKey() + " (" + worst.getValue() + ")");
        }

        System.out.println();
    }
    

    /*============================================================
     *  INPUT HELPERS
     *============================================================*/

    /**
     * Reads an integer from the user, ensuring it falls within the specified range.
     *
     * @param low  the minimum accepted value
     * @param high the maximum accepted value
     * @return a validated integer within the given bounds
     */
    private int readIntInRange(int low, int high) {
        while (true) {
            try {
                int n = Integer.parseInt(scanner.nextLine());
                if (n >= low && n <= high) {
                    return n;
                }
            } catch (Exception ignored) {
            }
            System.out.printf("Enter a number between %d and %d: ", low, high);
        }
    }
}
