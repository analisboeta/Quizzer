package org.academiadecodigo.quizzer.server;

import org.academiadecodigo.quizzer.finalvars.FinalVars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Neiva on 10-11-2016.
 */
public class Server {

    private Socket clientSocket;
    private int portNumber;
    private Vector<ServerConnection> playersList;
    private int maxNrOfPlayers;
    private ExecutorService poolRejectPlayers = Executors.newFixedThreadPool(2);


    public Server() {

        setPortNumber();
        maxNrOfPlayers = FinalVars.MAX_NR_PLAYERS;
        playersList = new Vector<>();
        createPlayersNr();
        startServer();
    }

    public Server(int portNumber, int maxNrOfPlayers) {

        this.maxNrOfPlayers = maxNrOfPlayers;
        this.portNumber = portNumber;
        playersList = new Vector<>();
        createPlayersNr();
        startServer();
    }

    private void setPortNumber() {

        System.out.print("Type the Port number: ");
        BufferedReader port = new BufferedReader(new InputStreamReader(System.in));

        try {
            portNumber = Integer.parseInt(port.readLine());
        } catch (IOException | NumberFormatException e) {
            portNumber = FinalVars.DEFAULT_PORT_NR;
        }
    }

    private void startServer() {

        ServerConnection sC;
        ExecutorService pool = Executors.newFixedThreadPool(maxNrOfPlayers);

        System.out.println("Server listening on port " + portNumber + "\nover...");

        broadcast("Waiting for more playersList on port..." + portNumber);

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            while (true) {

                clientSocket = serverSocket.accept();

                if (playersList.size() < maxNrOfPlayers) {
                    sC = new ServerConnection(clientSocket, this);
                    playersList.addElement(sC);
                    System.out.println(clientSocket + " connected!\nTotal: " + playersList.size());
                    pool.submit(sC);
                    continue;
                }
                rejectClient(clientSocket);
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

    private void rejectClient(Socket clientSocket) {

        poolRejectPlayers.submit(() -> { // "() ->" same as "new Runnable()"

            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(FinalVars.REJECTED_PLAYER_MESSAGE);
                out.flush();
                clientSocket.close();
            } catch (IOException e) {
                e.getMessage();
                e.printStackTrace();
            }

        });
    }

    public void broadcast(String message) {

        for (ServerConnection client : playersList) {
            client.sendMessage(message + "\n");
        }
    }

    public void stopConnection(ServerConnection client, String playerName) {

        if (!playersList.isEmpty()) {
            broadcast("\n" + (char) 27 + "[30;41;1m[" + playerName + "] as quit!" + (char) 27 + "[0m");
        }
        playersList.removeElement(client);
        System.out.println("Remaining playersList: " + playersList.size());
    }

    public int getNrOfMissingPlayers() {
        return maxNrOfPlayers - playersList.size();
    }
}