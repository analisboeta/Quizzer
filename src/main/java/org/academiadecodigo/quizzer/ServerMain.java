package org.academiadecodigo.quizzer;

import org.academiadecodigo.quizzer.game.Game;
import org.academiadecodigo.quizzer.server.Server;

/**
 * Created by Neiva on 07-11-2016.
 */
public class ServerMain {

    public static void main(String[] args) {


        Server tcpServer = null;

        if (args.length < 1) {
            tcpServer = new Server();
        }else{
            tcpServer = new Server(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
        }

//        new Game(new Server());

    }
}
