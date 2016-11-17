package org.academiadecodigo.quizzer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Neiva on 10-11-2016.
 */
public class Server {

    private final int MAX_THREADS = 4;
    private Socket clientSocket;
    private int portNumber;
    private Vector<ServerConnection> players;

    public Server() {

        setPortNumber();
        players = new Vector<>();
        startServer();
    }

    public Server(int portNumber) {

        this.portNumber = portNumber;
        players = new Vector<>();
        startServer();
    }

    private void setPortNumber() {

        System.out.print("Type the Port number: ");
        BufferedReader port = new BufferedReader(new InputStreamReader(System.in));

        try {
            portNumber = Integer.parseInt(port.readLine());
        } catch (IOException | NumberFormatException e) {
            portNumber = 6666;
        }
    }

    private void startServer() {

        ServerConnection sC;
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        broadcast("Waiting for more players on port...");

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            while (true) {
                clientSocket = serverSocket.accept();
                sC = new ServerConnection(clientSocket, this);
                players.addElement(sC);
                System.out.println(clientSocket + " connected!\nTotal: " + players.size());
                pool.submit(sC);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Bye Bye!!!");
        }
    }

    public void broadcast(String questionBlock) {

        for (ServerConnection client : players) {
            client.sendQuestion(questionBlock + "\n");
        }
    }

    public void stopConnection(ServerConnection client, String playerName) {

        if (!players.isEmpty()) {
            broadcast("\n" + (char) 27 + "[30;41;1m[" + playerName + "] as quit!" + (char) 27 + "[0m");
        }
        players.removeElement(client);
        System.out.println("Remaining players: " + players.size());
    }
}