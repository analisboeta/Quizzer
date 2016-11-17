package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.finalvars.FinalVars;

/**
 * Created by codecadet on 15/11/16.
 */
public class Game {

    private String[] question;
    private QuestionHandler handler;


    public boolean verifyAnswer(String answer) {

        return answer.equalsIgnoreCase(question[FinalVars.CHAR_ANSWER_INDEX]);
    }

    public void scoreBoard() {

        System.out.println("broadcast score of all players");
    }

    public void printQuestion() {

        if (handler == null) {
            handler = new QuestionHandler();
            handler.loadQuestions();
        }
        question = handler.pickQuestion();
        System.out.println("question size: " + question.length);
        System.out.println("Question: " + question[0]);
        System.out.println("A: " + question[1]);
        System.out.println("B: " + question[2]);
        System.out.println("C: " + question[3]);
        System.out.println("D: " + question[4]);
    }
}
