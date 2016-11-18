package org.academiadecodigo.quizzer.server;

import org.academiadecodigo.quizzer.finalvars.FinalVars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Neiva on 10-11-2016.
 */
public class Server {

    private Socket clientSocket;
    private int portNumber;
    private Hashtable<InetAddress, Player> playersList;
    private int maxNrOfPlayers;
    private ExecutorService poolRejectPlayers = Executors.newFixedThreadPool(2);


    public Server() {

        setPortNumber();
        maxNrOfPlayers = FinalVars.MAX_NR_PLAYERS;
        playersList = new Hashtable<>();
        startServer();
    }

    public Server(int portNumber, int maxNrOfPlayers) {

        this.maxNrOfPlayers = maxNrOfPlayers;
        this.portNumber = portNumber;
        playersList = new Hashtable<>();
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

        Player player;
        ExecutorService pool = Executors.newFixedThreadPool(maxNrOfPlayers);

        System.out.println("Server listening on port " + portNumber + "\nWaiting for players...");

        broadcast("Waiting for more playersList on port..." + portNumber);

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            while (true) {

                clientSocket = serverSocket.accept();

                if (playersList.size() < maxNrOfPlayers && !playersList.containsKey(clientSocket.getInetAddress())) { // TODO: 18/11/16 build 2 server jars - LAN and WAN
                    player = new Player(clientSocket, this);
                    playersList.put(clientSocket.getInetAddress(), player);
                    System.out.println(clientSocket + " connected!\nTotal: " + playersList.size());
                    pool.submit(player);
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

        for (Player client : playersList.values()) {
            client.sendMessage(message + "\n");
        }
    }

/*
    public void stopConnection(String playerName) {

        if (!playersList.isEmpty()) {
            broadcast("\n" + (char) 27 + "[30;41;1m[" + playerName + "] as quit!" + (char) 27 + "[0m");
        }
        ;
        System.out.println(playersList.remove(clientSocket.getInetAddress()) + "loose connection.\nRemaining players: " + playersList.size());
    }
*/

    public int getNrOfMissingPlayers() {
        return maxNrOfPlayers - playersList.size();
    }
}