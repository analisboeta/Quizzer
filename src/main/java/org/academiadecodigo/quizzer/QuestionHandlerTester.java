package org.academiadecodigo.quizzer;

import org.academiadecodigo.quizzer.game.QuestionHandler;

/**
 * Created by Neiva on 16-11-2016.
 */
public class QuestionHandlerTester {

    public static void main(String[] args) {


        QuestionHandler game = new QuestionHandler();
        game.loadQuestions();
        toPrint(game.pickQuestion());
        toPrint(game.pickQuestion());
        toPrint(game.pickQuestion());
        toPrint(game.pickQuestion());
        toPrint(game.pickQuestion());
        toPrint(game.pickQuestion());

    }

    private static void toPrint(String[] qBlock) {

        for (int i = 0; i < qBlock.length - 1; i++) {
            String toPrint = (i == 0) ? "\n" + qBlock[i] + "\n" : qBlock[i] + "\t";
            System.out.println(toPrint);
        }
    }
}
