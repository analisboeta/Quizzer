package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.finalvars.FinalVars;

/**
 * Created by codecadet on 15/11/16.
 */
public class Game {

    private String[] question;
    private QuestionHandler handler;

    /**
     * Compares the answer given by the user with the correct answer.
     *
     * @param answer the input from the player
     * @return boolean
     */
    public boolean verifyAnswer(String answer) {

        return answer.equalsIgnoreCase(question[FinalVars.CORRECT_ANSWER_LETTER_INDEX]);
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
     * @return A question.
     */
    private String questionBuilder() {

        return "Question: " + question[0]+
                "\nA: " + question[1]+
                "\nB: " + question[2]+
                "\nC: " + question[3]+
                "\nD: " + question[4];
    }
}
