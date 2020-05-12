package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.parser.Messages;
import it.polimi.ingsw.server.parser.NetworkParser;
import org.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author thomas
 * VirtualView class notifies Controller (as an Observable) and is notified by Model (as an observer).
 * Each VirtualVieww is a Runnable object associated to a socket.
 */
public class VirtualView extends ViewObservable implements Runnable {

    private final Socket socket;
    private PrintWriter out;
    private String nickname;

    public VirtualView(Socket socket, Controller controller) {
        setObservers(controller);
        this.socket = socket;

    }



    protected void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }


    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            boolean connected = socket.isConnected();

            while(connected) {
                String message = in.nextLine();

                System.out.println("Received message from " + socket.getRemoteSocketAddress() + ": " + message);

                connected = handleMessage(message);


            }

            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void send(String message) {
        out.println(message);
    }

    /**
     * Parses Message from client and notifies Controller according to message data.
     * @param message String read from socket. Supposed to be a JSON String
     * @return true if client is still connected. False if client wants to disconnect
     */
    private boolean handleMessage(String message) {

        boolean connected = true;

        try{
            NetworkParser parser = new NetworkParser(message);

            Coordinates src, dst;
            Coordinates builder1, builder2;
            String date, color;
            int numberOfplayers;


            switch (parser.getRequest()) {
                case Messages.MOVE:
                    src = parser.getSrcCoordinates();
                    dst = parser.getDstCoordinates();
                    notifyMove(nickname, src, dst);
                    break;

                case Messages.BUILD:
                    src = parser.getSrcCoordinates();
                    dst = parser.getDstCoordinates();
                    notifyBuild(nickname, src, dst, parser.getBuildDome());
                    break;

                case Messages.SET_BUILDERS:
                    builder1 = parser.getSrcCoordinates();
                    builder2 = parser.getDstCoordinates();
                    notifySetupBuilders(nickname, builder1, builder2);
                    break;

                case Messages.SET_COLOR:
                    color = parser.getColor();
                    notifyColorChoice(nickname, color);
                    break;

                case Messages.ADD_PLAYER:
                    //drops redundant add player command
                    if(this.nickname != null) {
                        date = parser.getDate();
                        setNickname(parser.getName());

                        notifyNewPlayer(this.nickname, date);
                    } else {
                        send(Messages.errorMessage("nickname already set"));
                    }
                    break;

                case Messages.SET_GOD_CARD:
                    notifyGodCardChoice(nickname, parser.getGodCardName());
                    break;


                case Messages.SET_NUMBER_OF_PLAYERS:
                    numberOfplayers = parser.getNumberOfPlayers();
                    notifyNumberOfPlayers(numberOfplayers);
                    break;

                case Messages.SET_STEP_CHOICE:
                    String stepChoice = parser.getStepChoice();
                    notifyStepChoice(nickname, stepChoice);
                    break;

                case Messages.DISCONNECT:
                    connected = false;
                    notifyDisconnection(nickname);
                    send(Messages.disconnect());

            }
        } catch (JSONException e) {
            send(Messages.errorMessage(e.getMessage()));
        }

        return connected;
    }


}
