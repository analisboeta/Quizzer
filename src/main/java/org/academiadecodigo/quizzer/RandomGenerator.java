package org.academiadecodigo.quizzer;

/**
 * Created by Neiva on 16-11-2016.
 */
public class RandomGenerator {

    public static int genRandom(int max) {

        return ((int)(Math.random() * (max))); // between [min; max[
    }

    public static int genRandom(int min, int max) {

        return (min + (int)(Math.random() * ((max - min)))); // between [min; max[
    }

}