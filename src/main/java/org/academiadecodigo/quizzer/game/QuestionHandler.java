package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.FileManager;
import org.academiadecodigo.quizzer.RandomGenerator;
import org.academiadecodigo.quizzer.finalvars.FinalVars;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Neiva on 16-11-2016.
 */


public class QuestionHandler {

    private LinkedList questions;

    public void loadQuestions() {

        try {
            questions = (questions == null) ? FileManager.readFile(FinalVars.QUESTIONS_THEME) : questions;
            System.out.println("Read file...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] pickQuestion() {

        String questionBlock;

        questionBlock = !questions.isEmpty() ? questions.remove(RandomGenerator.genRandom(questions.size())).toString() : "do ;  so ; me ; th ; ing ; ...";
        System.out.println("Questions size: " + questions.size()); //SOUT
        return questionBlock.split(FinalVars.QUESTION_BLOCK_SEPARATOR);
    }


}
