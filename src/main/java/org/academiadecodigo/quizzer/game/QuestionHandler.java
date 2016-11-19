package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.FileManager;
import org.academiadecodigo.quizzer.RandomGenerator;
import org.academiadecodigo.quizzer.constants.FinalVars;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Neiva on 16-11-2016.
 */


public class QuestionHandler {

    private LinkedList questions;


    /**
     * Loads Questions
     * If the questions are null, the file manager will select a theme. If not, it will return a question.
     * todo
     */
    public void loadQuestions() {

        try {
            questions = (questions == null) ? FileManager.readFile(FinalVars.QUESTIONS_THEME) : questions;
            System.out.println("Read file...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Picks a question
     todo If the question block is not empty, it will remove SOMETHING. OTHERWISE, SOMETHING ELSE WILL HAPPEN.
     * @return The question part of the question block.
     */
    public String[] pickQuestion() {

        String questionBlock;

        questionBlock = !questions.isEmpty() ? questions.remove(RandomGenerator.genRandom(questions.size())).toString() : "do ;  so ; me ; th ; ing ; ...";
        System.out.println("Questions size: " + questions.size()); //SOUT
        return questionBlock.split(FinalVars.QUESTION_BLOCK_SEPARATOR);
    }


}
