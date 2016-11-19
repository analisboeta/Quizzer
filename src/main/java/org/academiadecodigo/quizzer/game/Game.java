package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.constants.FinalVars;
import org.academiadecodigo.quizzer.constants.QuestionBuildType;
import org.academiadecodigo.quizzer.server.Server;

/**
 * Created by codecadet on 15/11/16.
 */
public class Game {

    private String[] question;
    private QuestionHandler handler;
    private Server server;
    private int maxNrOfPlayers;


    public Game(Server server, int maxNrOfPlayers) {
        this.server = server;
        this.maxNrOfPlayers = maxNrOfPlayers;
    }

    public Game(Server server) {
        this.server = server;
        maxNrOfPlayers = FinalVars.MAX_NR_PLAYERS;
    }

    public void startGame(String playerName) {

        if (server.getNrOfMissingPlayers() > 0) {
            server.broadcast("\n" + (char) 27 + "[30;42;1m" + playerName + " as joined the game" + (char) 27 +
                    "[0m\nStill waiting for " + server.getNrOfMissingPlayers() + " players");
        } else {
            server.broadcast("\n" + (char) 27 + "[30;42;1mStart the game" + (char) 27 + "[0m"); // TODO: 18/11/16 wait for player name
            server.broadcast(printQuestion());
            System.out.println("first question" + playerName);

        }
        System.out.println(playerName + " " );


    }

    public void gameFlow(String message, String playerName) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.getMessage();
            e.printStackTrace();
        }
        if (verifyAnswer(message)) {
            System.out.println("if correct answer" + playerName);
            server.broadcast(playerName + " won the round.\nCorrect answer: " + getCorrectAnswer());
            server.actualizeScores(playerName, FinalVars.POINTS_FOR_ANSWER);
        } else {
            System.out.println("else incorrect answer" + playerName);
            server.broadcast(playerName + "has missed. \nCorrect answer: " + getCorrectAnswer());
            server.actualizeScores(playerName, FinalVars.POINTS_FOR_ANSWER * -1);
        }
        server.broadcast(scoreBoard());
    }

    /**
     * Compares the answer given by the user with the correct answer.
     *
     * @param answer the input from the player
     * @return boolean
     */
    public boolean verifyAnswer(String answer) {

        return answer.equalsIgnoreCase(question[FinalVars.CORRECT_ANSWER_LETTER_INDEX]);
    }

    public String getCorrectAnswer() {

        return question[FinalVars.CORRECT_ANSWER_LETTER_INDEX];
    }


    /**
     * SCORE BOARD IT WILL EVENTUALLY DO SOMETHING MORE INTERESTING THAN A SOUT
     */
    public String scoreBoard() {

        System.out.println("broadcast score of all players");
        return "broadcast score of all players";
    }

    /**
     * Prints a question
     * If there is no handler, it will instantiate one and it will load the questions
     * Uses method from the handler to pick a question.
     */
    public String printQuestion() {

        if (handler == null) {
            handler = new QuestionHandler();
            handler.loadQuestions();
        }
        question = handler.pickQuestion();

        return questionBuilder();
    }


    /**
     * Builds questions. It places the question and the four options below.
     *
     * @return A question.
     */
    private String questionBuilder() {

        return QuestionBuildType.QUESTION.getText() + question[0] +
                QuestionBuildType.FIRSTANSWER.getText() + question[1] +
                QuestionBuildType.SECONDANSWER.getText() + question[2] +
                QuestionBuildType.THIRDANSWER.getText() + question[3] +
                QuestionBuildType.THIRDANSWER.getText() + question[4];
    }
}
