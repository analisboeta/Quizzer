package org.academiadecodigo.quizzer.server;

import org.academiadecodigo.quizzer.game.Game;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Neiva on 10-11-2016.
 */
public class PlayersConnection implements Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;
    private int score;
    private String name;
//    private Game game;

    PlayersConnection(Socket clientSocket, Server server) {

/*
        if (game == null) {
            game = new Game();
        }
*/
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

        String message;

        try {

            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.ISO_8859_1), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sendMessage("Type your name:");

            message = in.readLine();

            String pNumber = Thread.currentThread().getName().substring(Thread.currentThread().getName().length() - 1);
            Thread.currentThread().setName("[Player " + pNumber + "] " + message);
            name = Thread.currentThread().getName();
            server.startGame(name);

            /*if (server.getNrOfMissingPlayers() > 0) {
                server.broadcast("\n" + (char) 27 + "[30;42;1m" + Thread.currentThread().getName() + " as joined the game" + (char) 27 +
                        "[0m\nStill waiting for " + server.getNrOfMissingPlayers() + " players");
            } else {
                server.broadcast("\n" + (char) 27 + "[30;42;1mStart the game" + (char) 27 + "[0m"); // TODO: 18/11/16 wait for player name
                server.broadcast(game.printQuestion());
                System.out.println("first question" + Thread.currentThread().getName());

            }*/

                while ((message = in.readLine()) != null) {
                    System.out.println(clientSocket.getLocalAddress().getHostName() + clientSocket.getInetAddress() +
                            " | " + Thread.currentThread().getName() + ": " + message);
                    server.receiveClientMessage(message, name);

/*                    if (game.verifyAnswer(message)) {
                        System.out.println("if correct answer" + Thread.currentThread().getName());
                        server.broadcast(Thread.currentThread().getName() + " won the round.\nCorrect answer: " + game.getCorrectAnswer());
                        server.broadcast(game.scoreBoard());
                        Thread.sleep(1000);
                    } else {
                        System.out.println("else incorrect answer" + Thread.currentThread().getName());
                        server.broadcast(Thread.currentThread().getName() + "has missed. \nCorrect answer: " + game.getCorrectAnswer());
                        server.broadcast(game.scoreBoard());
                        Thread.sleep(1000);
                    }*/

//                    server.broadcast(game.printQuestion());

                }


        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();

        } finally {
            System.out.println("@@@");
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }

            } catch (IOException e) {
                e.getMessage();
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    public void setScore(int points) {
        score += points;
    }

    public String getName() {
        return name;
    }
}
