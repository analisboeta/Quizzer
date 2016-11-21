package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.constants.FinalVars;
import org.academiadecodigo.quizzer.constants.QuestionBuildType;
import org.academiadecodigo.quizzer.server.Server;

/**
 * Created by <Code Cadets_> Ana Lourenço, Hugo Neiva, Mariana Fazenda, Tomás Amaro on 21/11/16.
 */
public class Game {

    private String[] question;
    private QuestionHandler handler;
    private Server server;
    private int maxNrOfPlayers;
    private boolean questionAnswered;
    private int aux = 0;


    public Game(Server server, int maxNrOfPlayers) {
        this.server = server;
        this.maxNrOfPlayers = maxNrOfPlayers;
    }

    public Game(Server server) {
        this.server = server;
        maxNrOfPlayers = FinalVars.MAX_NR_PLAYERS;
    }


    /**
     * Game starter
     *
     * @param playerName name of the player.
     * If the minimum number of players are connected, the server sends a "Start game"
     * message and prints a question to the players.
     */
    public synchronized void startGame(String playerName) {


        aux++;
        if (server.getNrOfMissingPlayers() > 0 || aux < FinalVars.MAX_NR_PLAYERS) {
            return;
        } else {
            System.out.println("Question Answered" + questionAnswered);
            server.broadcast("\n" + (char) 27 + "[30;42;1mStart the game" + (char) 27 + "[0m");
            server.broadcast(printQuestion());
        }
    }

    /**
     * Follows the game logic
     *
     * @param message    - the answer given by the player
     * @param playerName - the name of the player.
     * In the player inputs a name and an answer: this method tests if the player has made an input.
     * If so, it will verify if the question has been answered. If answered correctly, increments the scoreboard.
     * Otherwise, a "wrong answer" message will be printed out to the player.
     */
    public synchronized void gameFlow(String message, String playerName) {

        System.out.println("someone answered: " + questionAnswered);

        try {
            if (!message.equals(FinalVars.TIME_RUN_OUT_STRING) && !playerName.equals(FinalVars.TIME_RUN_OUT_STRING)) {
                if (questionAnswered) {
                    if (verifyAnswer(message)) {
                        System.out.println("if correct answer" + playerName);
                        server.broadcast(playerName + " won the round.\nCorrect answer: " + getCorrectAnswer());
                        server.actualizeScores(playerName, FinalVars.POINTS_FOR_ANSWER);

                    } else {
                        System.out.println("else incorrect answer" + playerName);
                        server.broadcast(playerName + " has missed. \nCorrect answer: " + getCorrectAnswer());
                        server.actualizeScores(playerName, (-FinalVars.POINTS_FOR_ANSWER));
                    }
                }
            } else {
                System.out.println("answer timeout" + playerName);

            }
            server.printScoreboard();
            wait(1000);
            questionAnswered = false;
            server.broadcast(printQuestion());

        } catch (InterruptedException e) {
            e.getMessage();
            e.printStackTrace();
        }
        System.out.println("alguém respondeu fim método: " + questionAnswered);

    }

    /**
     * Verifies answer
     *
     * @param answer - the input from the player
     * @return boolean - true when the answer is correct
     * Transforms the user input to an uppercase string and then compares it with the correct answer present in the question array.
     */
    private boolean verifyAnswer(String answer) {

        return answer.toUpperCase().equals(question[FinalVars.CORRECT_ANSWER_LETTER_INDEX]);
    }

    /**
     * @return correct answer string
     */
    private String getCorrectAnswer() {

        return question[FinalVars.CORRECT_ANSWER_LETTER_INDEX];
    }


    /**
     * Prints a question
     *
     * @return - question string
     * If there is no handler, it will instantiate one and it will load the questions
     * Uses method from the handler to pick a question.
     */
    private String printQuestion() {

        if (handler == null) {
            handler = new QuestionHandler();
            handler.loadQuestions();
        }
        question = handler.pickQuestion();

        return questionBuilder();
    }

    /**
     * Builds questions
     *
     * @return - a complete question set
     * It places the question and its four possible answers below
     */
    private String questionBuilder() {

        return String.format("%s \n%-30s %s \n%-30s %s",
                (char) 27 + "[37;40;1m" + question[0] + (char) 27 + "[0m",
                (char) 27 + "[31;1m" + QuestionBuildType.FIRSTANSWER.getText() + (char) 27 + "[0m" + question[1],
                (char) 27 + "[31;1m" + QuestionBuildType.SECONDANSWER.getText() + (char) 27 + "[0m" + question[2],
                (char) 27 + "[31;1m" + QuestionBuildType.THIRDANSWER.getText() + (char) 27 + "[0m" + question[3],
                (char) 27 + "[31;1m" + QuestionBuildType.FOURTHANSWER.getText() + (char) 27 + "[0m" + question[4]);

    }

    public int getMaxNrOfPlayers() {

        return maxNrOfPlayers;
    }

    public boolean isQuestionAnswered() {

        return questionAnswered;
    }

    public void setQuestionAnswered(boolean questionAnswered) {

        this.questionAnswered = questionAnswered;
    }
}
