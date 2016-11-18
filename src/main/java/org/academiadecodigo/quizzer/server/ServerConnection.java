package org.academiadecodigo.quizzer.server;

import org.academiadecodigo.quizzer.game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Neiva on 10-11-2016.
 */
public class ServerConnection implements Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;
    private int maxPlayers;
    private Game game;

    ServerConnection(Socket clientSocket, Server server) {

        if (game == null) {
            game = new Game();
        }
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

        String message;

        try {

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sendMessage("Type your name:");

            message = in.readLine();
            String pNumber = Thread.currentThread().getName().substring(Thread.currentThread().getName().length()-1);
            System.out.println(pNumber);
            Thread.currentThread().setName("[Player " + pNumber + "] " + message);
            server.broadcast("\n" + (char) 27 + "[30;42;1m" + Thread.currentThread().getName() + " as joined the game" + (char) 27 + "[0m");

            while ((message = in.readLine()) != null) {
                System.out.println(clientSocket.getLocalAddress().getHostName() + clientSocket.getInetAddress() +
                        " | " + Thread.currentThread().getName() + ": " + message);

                if (game.verifyAnswer(message)) {
                    server.broadcast(Thread.currentThread().getName() + "won. Answer: " + message);
                    server.broadcast(game.scoreBoard());
                    Thread.sleep(1000);
                    server.broadcast(game.printQuestion());

                }

            }

        } catch (IOException | InterruptedException e) {
            e.getMessage();
            e.printStackTrace();

        } finally {
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
}
