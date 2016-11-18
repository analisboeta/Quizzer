package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.finalvars.FinalVars;

/**
 * Created by codecadet on 15/11/16.
 */
public class Game {

    private String[] question;
    private QuestionHandler handler;


    public boolean verifyAnswer(String answer) {

        return answer.equalsIgnoreCase(question[FinalVars.CORRECT_ANSWER_LETTER_INDEX]);
    }

    public String scoreBoard() {

        System.out.println("broadcast score of all players");
        return "broadcast score of all players";
    }

    public String printQuestion() {

        if (handler == null) {
            handler = new QuestionHandler();
            handler.loadQuestions();
        }
        question = handler.pickQuestion();

        return questionBuilder();
    }

    private String questionBuilder() {

        return "Question: " + question[0]+
                "\nA: " + question[1]+
                "\nB: " + question[2]+
                "\nC: " + question[3]+
                "\nD: " + question[4];
    }
}
