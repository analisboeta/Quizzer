package org.academiadecodigo.quizzer.constants;

/**
 * Created by codecadet on 19/11/16.
 */
public enum QuestionBuildType {

    QUESTION("Question "),
    FIRSTANSWER("\nA: "),
    SECONDANSWER("\nB: "),
    THIRDANSWER("\nC: "),
    FOURTHANSWER("\nD: ");

    QuestionBuildType(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }


}
