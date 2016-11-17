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

        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

        String message;

        try {

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sendQuestion("Type Playername:");

            message = in.readLine();
            Thread.currentThread().setName(message);
            server.broadcast("\n" + (char) 27 + "[30;42;1m[" + message + "] as joined the game" + (char) 27 + "[0m");

            while ((message = in.readLine()) != null) {
                System.out.println(clientSocket.getLocalAddress().getHostName() + clientSocket.getInetAddress() +
                        " | " + Thread.currentThread().getName() + ": " + message);

                if (message.startsWith("_")) {
                    if (message.equalsIgnoreCase("_q_")) {
                        server.stopConnection(this, Thread.currentThread().getName());
                    }
                } else {
                    server.broadcast((char) 27 + "[31;1m" + Thread.currentThread().getName() + ": " + (char) 27 + "[0m" + message);
                }
            }

        } catch (IOException e) {
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

    public void sendQuestion(String questionBlock) {
        out.println(questionBlock);
        out.flush();
    }
}
