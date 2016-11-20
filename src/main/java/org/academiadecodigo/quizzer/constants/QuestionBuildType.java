package org.academiadecodigo.quizzer.constants;

/**
 * Created by codecadet on 19/11/16.
 */
public enum QuestionBuildType {

    FIRSTANSWER("A: "),
    SECONDANSWER("B: "),
    THIRDANSWER("C: "),
    FOURTHANSWER("D: ");

    QuestionBuildType(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }


}
