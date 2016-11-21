package org.academiadecodigo.quizzer.game;

import org.academiadecodigo.quizzer.FileManager;
import org.academiadecodigo.quizzer.RandomGenerator;
import org.academiadecodigo.quizzer.constants.FinalVars;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by <Code Cadets_> Ana Lourenço, Hugo Neiva, Mariana Fazenda, Tomás Amaro on 21/11/16.
 */
public class QuestionHandler {

    private LinkedList questions;


    /**
     * Loads Questions
     *
     * If the questions are null, the file manager will select a theme. If not, it will return a question.
     * */
    public void loadQuestions() {

        // Even though our initial batch of questions is General Knowledge,
        // the next step will be to implement a thematic Quizz, thus we left that possibility open in this method
        try {
            questions = (questions == null) ? FileManager.readFile(FinalVars.QUESTIONS_THEME) : questions;
            System.out.println("Read file...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Picks a question
     *
     * @return - an array with all the elements of a single question
     *
     * If our container of questions isn't empty, a random question will be removed into a questionBlock string (a string
     * with a question plus all the possible answers and the right answer) and it will be split into a question array
     * and then returned.
     * If it is, then it will {do;so;me;thing;...} (just a crafty solution to correct a stupid bug...)
     */
    public String[] pickQuestion() {

        String questionBlock;

        questionBlock = !questions.isEmpty() ? questions.remove(RandomGenerator.genRandom(questions.size())).toString() : "do ;  so ; me ; th ; ing ; ...";
        System.out.println("Questions size: " + questions.size()); //SOUT
        return questionBlock.split(FinalVars.QUESTION_BLOCK_SEPARATOR);
    }


}
