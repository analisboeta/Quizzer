package org.academiadecodigo.quizzer;

import org.academiadecodigo.quizzer.game.Game;

/**
 * Created by Neiva on 16-11-2016.
 */
public class GameTester {

    public static void main(String[] args) {


        Game game = new Game();

        for (int i = 0; i < 6; i++) {
            game.printQuestion();
            System.out.println("answer: " + game.verifyAnswer("D"));
        }

    }
}
