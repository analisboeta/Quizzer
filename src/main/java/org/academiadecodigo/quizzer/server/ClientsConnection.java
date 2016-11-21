package org.academiadecodigo.quizzer.server;

import org.academiadecodigo.quizzer.constants.FinalVars;
import org.academiadecodigo.quizzer.game.Game;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by <Code Cadets_> Ana Lourenço, Hugo Neiva, Mariana Fazenda, Tomás Amaro on 21/11/16.
 */
class ClientsConnection implements Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;
    private int score;
    private String name;


    ClientsConnection(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {

        String message;

        try {

            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            sendMessage("\n\n"+
                    "\t    ██████╗ ██╗   ██╗██╗███████╗███████╗███████╗██████╗ \n" +
                    "\t   ██╔═══██╗██║   ██║██║╚══███╔╝╚══███╔╝██╔════╝██╔══██╗\n" +
                    "\t   ██║   ██║██║   ██║██║  ███╔╝   ███╔╝ █████╗  ██████╔╝\n" +
                    "\t   ██║▄▄ ██║██║   ██║██║ ███╔╝   ███╔╝  ██╔══╝  ██╔══██╗\n" +
                    "\t   ╚██████╔╝╚██████╔╝██║███████╗███████╗███████╗██║  ██║\n" +
                    "\t    ╚══▀▀═╝  ╚═════╝ ╚═╝╚══════╝╚══════╝╚══════╝╚═╝  ╚═╝\n" +
                "\t\t\t\t    by \n"+
            "\tANA LOURENÇO -- HUGO NEIVA -- MARIANA FAZENDA -- TOMÁS AMARO\n\n");

            Thread.sleep(3500);
            sendMessage((char) 27 + "[30;42;1mType your name:" + (char) 27 + "[0m");

            message = in.readLine();

            String pNumber = Thread.currentThread().getName().substring(Thread.currentThread().getName().length() - 1);
            Thread.currentThread().setName("[Player " + pNumber + "] " + message);
            name = Thread.currentThread().getName();

            if (!name.equals("")){
            server.startGame(name);
            }

            while ((message = in.readLine()) != null) {
                server.serverSetQuestionAnswered(true);
                System.out.println(clientSocket.getLocalAddress().getHostName() + clientSocket.getInetAddress() +
                        " | " + Thread.currentThread().getName() + ": " + message);
                    server.receiveClientMessage(message, name);
            }
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    void sendMessage(String message) {

        out.println(message);
        out.flush();
    }

    void setScore(int points) {

        score = score + points > 0 ? score + points : 0;
    }

    String getName() {

        return name;
    }

    int getScore() {

        return score;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

}
