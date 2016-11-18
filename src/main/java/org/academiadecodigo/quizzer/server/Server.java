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
    private String[] playersNr;
    private ExecutorService poolRejectPlayers;// = Executors.newFixedThreadPool(2);

    /**
     * Server constructor.
     * Sets a port number and the final number od players.
     * Creates a player list.
     * Calls a method to fill the list.
     * Starts the server.
     */
    public Server() {

        setPortNumber();
        maxNrOfPlayers = FinalVars.MAX_NR_PLAYERS;
        playersList = new Vector<>();
        createPlayersNr();
        startServer();
    }

    /**
     * Server Constructor.
     * @param portNumber the port number in which all connections will be set on.
     * @param maxNrOfPlayers the maximum number of players that will be allowed to connect at a time.
     * Creates player list.
     * Calls a method to fill the list.
     * Starts the server.
     */
    public Server(int portNumber, int maxNrOfPlayers) {

        this.maxNrOfPlayers = maxNrOfPlayers;
        this.portNumber = portNumber;
        playersList = new Vector<>();
        createPlayersNr();
        startServer();
    }

    /**
     * Sets the port Number and the user input Stream.
     * It will be set as a final variable.
     */
    private void setPortNumber() {

        System.out.print("Type the Port number: ");
        BufferedReader port = new BufferedReader(new InputStreamReader(System.in));

        try {
            portNumber = Integer.parseInt(port.readLine());
        } catch (IOException | NumberFormatException e) {
            portNumber = FinalVars.DEFAULT_PORT_NR;
        }
    }

    /**
     * Server starter.
     * Creates a pool with as many threads as the maximum number of players.
     * Creates a server socket to establish a connection in the port number previously assigned.
     * When accepting a new client into the server socket, it will redirect it to a new client socket.
     * While the number of players doesn't reach the maximum number of connections previously established,
     * a new server connection will be created and a new player is added to the list and the current thread will be added to the pool.
     * Otherwise, if the maximum number of players has been reached, the client will be rejected.
     */
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

    /**
     * Rejects a client.
     * @param clientSocket to identify the client that tries to connect over the maximum number of clients established.
     * The number of players that try to connect after the maximum number of players has been reached, will be inserted
     * in a new pool. An output stream is set to send a "try again later" type message and the connection will be immediatly over.
     */
    private void rejectClient(Socket clientSocket) {

        poolRejectPlayers = Executors.newFixedThreadPool(2);
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


    /**
     * Broadcasts message to every client.
     * @param message that will be sent as a response to clients.
     * For every client on the player list, the server will send a message.
     */
    public void broadcast(String message) {

        for (ServerConnection client : playersList) {
            client.sendMessage(message + "\n");
        }
    }

    /**
     * Stops the connection.
     * @param client is a server connection.
     * @param playerName
     * If the list of players is not empty it will broadcast a message with the name of the player when he quits, removing him from the list.
     * Otherwise it will print out then names of players still in the game.
     */
    public void stopConnection(ServerConnection client, String playerName) {

        if (!playersList.isEmpty()) {
            broadcast("\n" + (char) 27 + "[30;41;1m[" + playerName + "] as quit!" + (char) 27 + "[0m");
        }
        playersList.removeElement(client);
        System.out.println("Remaining playersList: " + playersList.size());
    }

    /**
     * Sets the player number for each player that successfully connects to the server.
     */
    private void createPlayersNr() {
        playersNr = new String[maxNrOfPlayers];
        for (int i = 0; i < maxNrOfPlayers; i++) {
            playersNr[i] = "[Player " + (i + 1) + "] ";
        }
    }
}