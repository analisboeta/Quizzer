package org.academiadecodigo.quizzer.server;

import org.academiadecodigo.quizzer.constants.FinalVars;
import org.academiadecodigo.quizzer.game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by <Code Cadets_> Ana Lourenço, Hugo Neiva, Mariana Fazenda, Tomás Amaro on 21/11/16.
 */
public class Server {

    private Socket clientSocket;
    private int portNumber;
    private Hashtable<InetAddress, ClientsConnection> clientsList;
    private int maxNrOfClients;
    private ExecutorService poolRejectPlayers;
    private Game game;

    /**
     * Server constructor.
     * Sets a port number and the final number of players.
     * Creates a player list.
     * Calls a method to fill the list.
     * Starts the server.
     */
    public Server() {

        setPortNumber();
        game = new Game(this);
        game.setMaxNrOfPlayers(maxNrOfClients);
        clientsList = new Hashtable<>();
        startServer();
    }

    /**
     * Server Constructor.
     *
     * @param portNumber     the port number in which all connections will be set on.
     * @param maxNrOfClients the maximum number of players that will be allowed to connect at a time.
     *                       Creates player list.
     *                       Calls a method to fill the list.
     *                       Starts the server.
     */
    public Server(int portNumber, int maxNrOfClients) {

        this.portNumber = portNumber;
        game = new Game(this);
        this.maxNrOfClients = maxNrOfClients;
        clientsList = new Hashtable<>();
        startServer();
    }

    /**
     * Sets the port number and the user input Stream.
     * The port number will be set as a final variable.
     */
    private void setPortNumber() {

        try {
        System.out.print("Type the Port Number: ");
        Scanner scanner = new Scanner(System.in);
        portNumber = Integer.parseInt(scanner.nextLine());

        System.out.print("Number of Players: ");
        maxNrOfClients = Integer.parseInt(scanner.nextLine());

        } catch (NumberFormatException e) {
            portNumber = FinalVars.DEFAULT_PORT_NR;
            maxNrOfClients = FinalVars.MAX_NR_PLAYERS;
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

        ClientsConnection clientsConnection;
        ExecutorService pool = Executors.newFixedThreadPool(maxNrOfClients);

        System.out.println("Server listening on port " + portNumber + "\nWaiting for players...");

        broadcast("Waiting for more clientsList on port..." + portNumber);

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            while (true) {
                clientSocket = serverSocket.accept();

                if (clientsList.size() < maxNrOfClients && !clientsList.containsKey(clientSocket.getInetAddress())){ // TODO: 18/11/16 build 2 server jars - LAN and WAN
                    clientsConnection = new ClientsConnection(clientSocket, this);
                    clientsList.put(clientSocket.getInetAddress(), clientsConnection);
                    System.out.println(clientSocket + " connected!\nTotal: " + clientsList.size());
                    pool.submit(clientsConnection);
                    continue;
                }
                rejectClient(clientSocket);
            }
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                System.exit(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Bye Bye!!!");
        }
    }

    /**
     * Rejects a client.
     *
     * @param clientSocket to identify the client that tries to connect over the maximum number of clients established.
     *                     The number of players that try to connect after the maximum number of players has been reached, will be inserted
     *                     in a new pool. An output stream is set to send a "try again later" type message and the connection will be immediatly over.
     */
    private void rejectClient(Socket clientSocket) {

        if (poolRejectPlayers == null) {
            poolRejectPlayers = Executors.newFixedThreadPool(2);
        }
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
     *
     * @param message that will be sent as a response to clients.
     *                For every client on the player list, the server will send a message.
     */
    public synchronized void broadcast(String message) {

        for (ClientsConnection client : clientsList.values()) {
            client.sendMessage("\n" + message);
        }
    }

    public synchronized void sendPrivateMessage(String message, String PlayerName) {

        for (ClientsConnection client : clientsList.values()) {
            if (clientsList.values().equals(PlayerName))
                client.sendMessage("\n" + message);
        }
    }


    /**
     * Stops the connection.
     *
     * @param client     is a server connection.
     * @param playerName If the list of players is not empty it will broadcast a message with the name of the player when he quits, removing him from the list.
     *                   Otherwise it will print out then names of players still in the game.
     */
    public void stopConnection(ClientsConnection client, String playerName) {
        if (!clientsList.isEmpty()) {
            broadcast("\n" + (char) 27 + "[30;41;1m[" + playerName + "] as quit!" + (char) 27 + "[0m");
        }
        System.out.println(clientsList.remove(clientSocket.getInetAddress()) + "loose connection.\nRemaining players: " + clientsList.size());
    }

    /**
     * Ends the game.
     * Closes all client connections and clears the list.
     */
    public void endGame(){
        try {

            clientSocket.close();
            for (ClientsConnection client: clientsList.values()) {
                client.getClientSocket().close();
            }

            clientsList.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int getNrOfMissingPlayers() {

        return maxNrOfClients - clientsList.size();
    }

    /**
     * Starts the game.
     * @param clientName - players input.
     * Starts the game for one player.
     */
    void startGame(String clientName) {

        game.startGame(clientName);
    }

    /**
     * Receives a message.
     * @param message - input by the player.
     * @param playerName - players name.
     * Checks to see if the player answered. If so, continue the game flow.
     */
    synchronized void receiveClientMessage(String message, String playerName) {
        if (game.isQuestionAnswered()) {
            game.gameFlow(message, playerName);
        }
    }

    /**
     * Sets the score.
     * @param playerName - name of the player.
     * @param points - points set for each question.
     * For each player in the list, actualizes the score.
     */
    public void actualizeScores(String playerName, int points) {

        for (ClientsConnection client : clientsList.values()) {
            if (client.getName().equals(playerName)) {
                client.setScore(points);
                return;
            }
        }
    }

    /**
     * Prints the scoreboard.
     * For each client in the client list, prints the score.
     */
    public void printScoreboard() {

        String scoreBoard = "";
        for (ClientsConnection client : clientsList.values()) {
            scoreBoard += client.getName() + " | Score: " + client.getScore() + "\t";
        }
        broadcast(scoreBoard);
    }

    /**
     * Prints final scoreboard.
     * Prints every name in the clients list as well as the score for each one.
     * Sends the end logo and scoreboard for every player.
     */
    public void printFinalScoreboard() {
        String endLogo =
                "██╗    ██╗███████╗██╗     ██╗         ██████╗  ██████╗ ███╗   ██╗███████╗\n" +
                "██║    ██║██╔════╝██║     ██║         ██╔══██╗██╔═══██╗████╗  ██║██╔════╝\n" +
                "██║ █╗ ██║█████╗  ██║     ██║         ██║  ██║██║   ██║██╔██╗ ██║█████╗  \n" +
                "██║███╗██║██╔══╝  ██║     ██║         ██║  ██║██║   ██║██║╚██╗██║██╔══╝  \n" +
                " ███╔███╔╝███████╗███████╗███████╗    ██████╔╝╚██████╔╝██║ ╚████║███████╗\n" +
                " ╚══╝╚══╝ ╚══════╝╚══════╝╚══════╝    ╚═════╝  ╚═════╝ ╚═╝  ╚═══╝╚══════╝\n" +
                "                                                                         \n\n\n";

        String scoreBoard = "";
        for (ClientsConnection client : clientsList.values()) {
            scoreBoard += client.getName() + " | Score: " + client.getScore() + "\t";
        }
        broadcast(endLogo + scoreBoard);
    }

    public void serverSetQuestionAnswered(boolean questionAnswered) {
        game.setQuestionAnswered(questionAnswered);
    }


}
