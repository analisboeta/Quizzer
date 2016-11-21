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
    private boolean questionAnswered;
    private int playersConnected = 0;
    private int roundsCounter = 0;

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
     * @param playerName name of the player.
     *                   If the minimum number of players are connected, the server sends a "Start game" message and prints a question to the players.
     */
    public synchronized void startGame(String playerName) {

        server.broadcast("\n" + (char) 27 + "[30;42;1m" + playerName + " as joined the game" + (char) 27 +
                "[0m\nStill waiting for " + server.getNrOfMissingPlayers() + " players");

   /*            ("\n" + (char) 27 + "[30;42;1m" + playerName + " as joined the game" + (char) 27 +
                "[0m\nStill waiting for " + server.getNrOfMissingPlayers() + " players");*/

        playersConnected++;
        if (server.getNrOfMissingPlayers() > 0 || playersConnected < FinalVars.MAX_NR_PLAYERS) {
            return;
        } else {
            System.out.println("Question Answered" + questionAnswered);
            server.broadcast("\n" + (char) 27 + "[30;42;1mStart the game" + (char) 27 + "[0m");
            server.broadcast(printQuestion());
        }
    }

    /**
     *
     * @param message
     * @param playerName name of the player.
     */
    public synchronized void gameFlow(String message, String playerName) {


        /**
         *  boolean timeRunOut;
         * todo testar se esta porra do resposta dada funciona
         */

            try {
                roundsCounter++;
                if (!message.equals(FinalVars.TIME_RUN_OUT_STRING) && !playerName.equals(FinalVars.TIME_RUN_OUT_STRING)) {
                    if (questionAnswered) {
                        if (verifyAnswer(message)) {
                            System.out.println("alguém respondeu: " + questionAnswered);
                            //questionAnswered = true;
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
               /* server.broadcast("Time Out!!! Correct answer: " + getCorrectAnswer());
                server.actualizeScores("FinalVars.TIME_RUN_OUT", (-FinalVars.POINTS_FOR_ANSWER));*/
                }
                if(roundsCounter == FinalVars.TOTAL_OF_QUESTIONS){
                    server.printScoreboard();
                    //TODO END OF GAME STYLISH
                    Thread.sleep(3000);
                    server.endGame();
                }
                server.printScoreboard();
                wait(1000);
                questionAnswered = false;
                server.broadcast(printQuestion());

                /**
                 * todo timeout não funca.
                 * penso que terá que ver com a condição
                 */

            } catch (InterruptedException e) {
                e.getMessage();
                e.printStackTrace();
            }
            System.out.println("alguém respondeu fim método: " + questionAnswered);
        }

    /**
     * Compares the answer given by the user with the correct answer.
     *
     * @param answer the input from the player
     * @return boolean
     */

    private boolean verifyAnswer(String answer) {

        return answer.toUpperCase().equals(question[FinalVars.CORRECT_ANSWER_LETTER_INDEX]);
    }

    private String getCorrectAnswer() {

        return question[FinalVars.CORRECT_ANSWER_LETTER_INDEX];
    }

    /**
     * Prints a question
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
     * Builds questions. It places the question and the four options below.
     *
     * @return A question.
     */
    private String questionBuilder() {

        return String.format("%s \n%-30s %s \n%-30s %s",
                (char) 27 + "[37;40;1m" + question[0] + (char) 27 + "[0m",
                (char) 27 + "[31;1m" + QuestionBuildType.FIRSTANSWER.getText() + (char) 27 + "[0m" + question[1],
                (char) 27 + "[31;1m" + QuestionBuildType.SECONDANSWER.getText() + (char) 27 + "[0m" + question[2],
                (char) 27 + "[31;1m" + QuestionBuildType.THIRDANSWER.getText() + (char) 27 + "[0m" + question[3],
                (char) 27 + "[31;1m" + QuestionBuildType.FOURTHANSWER.getText() + (char) 27 + "[0m" + question[4]);
/*
        return (char) 27 + "[37;40;1m" + question[0] + (char) 27 + "[0m" +
                (char) 27 + "[31;1m" + QuestionBuildType.FIRSTANSWER.getText() + (char) 27 + "[0m" + question[1] +
                (char) 27 + "[31;1m" + QuestionBuildType.SECONDANSWER.getText() + (char) 27 + "[0m" + question[2] +
                (char) 27 + "[31;1m" + QuestionBuildType.THIRDANSWER.getText() + (char) 27 + "[0m" + question[3] +
                (char) 27 + "[31;1m" + QuestionBuildType.FOURTHANSWER.getText() + (char) 27 + "[0m" + question[4];
*/
    }

    public int getMaxNrOfPlayers() {

        return maxNrOfPlayers;
    }

    public void setMaxNrOfPlayers(int maxNrOfPlayers) {
        this.maxNrOfPlayers = maxNrOfPlayers;
    }

    public boolean isQuestionAnswered() {

        return questionAnswered;
    }

    public void setQuestionAnswered(boolean questionAnswered) {
        this.questionAnswered = questionAnswered;
    }
}
