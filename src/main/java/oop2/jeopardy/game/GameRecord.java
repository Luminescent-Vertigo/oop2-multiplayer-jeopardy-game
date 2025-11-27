package oop2.jeopardy.game;

/**
 * Represents a single record of a player's action in the game.
 * Stores details including the player, question, answer, correctness,
 * points earned, and running score.
 * 
 * <p>Used primarily by {@link GameHistory} for logging, reporting,
 * and scoreboard calculations.</p>
 * 
 * @author Kadie Bisnath - 816036090
 * @version 1.0
 */

public class GameRecord {

    /** Name of the player. */
    private final String playerName;

    /** Category of the question. */
    private final String category;

    /** Point value of the question. */
    private final int value;

    /** Text of the question. */
    private final String questionText;

    /** The answer provided by the player. */
    private final String userAnswer;

    /** The correct answer for the question. */
    private final String correctAnswer;

    /** Whether the player's answer was correct. */
    private final boolean correct;

    /** Points earned for this question (negative if incorrect). */
    private final int pointsEarned;

    /** Running score after this question. */
    private final int runningScore;


    /**
     * Constructs a new {@code GameRecord} with all relevant details.
     *
     * @param playerName    the player's name
     * @param category      the question's category
     * @param value         the point value of the question
     * @param questionText  the text of the question
     * @param userAnswer    the answer provided by the player
     * @param correctAnswer the correct answer
     * @param correct       whether the player's answer was correct
     * @param pointsEarned  points earned for this question
     * @param runningScore  player's running score after this question
     */
    public GameRecord(
            String playerName,
            String category,
            int value,
            String questionText,
            String userAnswer,
            String correctAnswer,
            boolean correct,
            int pointsEarned,
            int runningScore
    ) {
        this.playerName = playerName;
        this.category = category;
        this.value = value;
        this.questionText = questionText;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
        this.pointsEarned = pointsEarned;
        this.runningScore = runningScore;
    }


    /** 
     * Get the player's name.
     * 
     * @return the player's name */
    public String getPlayerName() {
        return playerName;
    }


    /** 
     * Get the question's category.
     * 
     * @return the question's category */
    public String getCategory() {
        return category;
    }


    /** Get the point value of the question
     * 
     * @return the point value of the question */
    public int getValue() {
        return value;
    }


    /** Get the question text
     * 
     * @return the question text */
    public String getQuestionText() {
        return questionText;
    }


    /** Get the player's answer
     * 
     * @return the player's answer */
    public String getUserAnswer() {
        return userAnswer;
    }


    /** Get the correct answer
     * 
     * @return the correct answer */
    public String getCorrectAnswer() {
        return correctAnswer;
    }


    /** Get whether the player's answer was correct
     * 
     * @return true if the player's answer was correct */
    public boolean isCorrect() {
        return correct;
    }


    /** Get points earned for this question
     * 
     * @return points earned for this question */
    public int getPointsEarned() {
        return pointsEarned;
    }


    /** Get the player's running score after this question
     * 
     * @return the player's running score after this question */
    public int getRunningScore() {
        return runningScore;
    }
    

    /**
     * Returns a string representation of the game record.
     *
     * @return formatted string with player, category, value, answer, correctness, points, and score
     */
    @Override
    public String toString() {
        return String.format(
            "[%-12s] (%s / %d pts)  Ans='%s' Correct=%s Earned=%d  Score=%d",
            playerName, category, value,
            userAnswer, correct, pointsEarned, runningScore
        );
    }

    
}
