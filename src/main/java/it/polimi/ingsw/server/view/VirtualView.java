package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.AbstractController;
import it.polimi.ingsw.server.controller.ConnectionObserver;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.network.Messages;
import it.polimi.ingsw.network.NetworkParser;
import org.json.JSONException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author thomas
 * VirtualView class notifies Controller (as an Observable) and is notified by Model (as an observer).
 * Each VirtualVieww is a Runnable object associated to a socket.
 */
public class VirtualView extends ViewObservable implements Runnable {

    private boolean connected;
    private final int pingDelay = 2;
    private final Socket socket;
    private PrintWriter out;
    private String nickname;
    private boolean registered = false;
    private ConnectionObserver connectionObserver;
    private ScheduledExecutorService scheduler;

    /**
     * Constructor that sets all its observers
     * @param socket connected to Client
     * @param controller that observes view events
     */
    public VirtualView(Socket socket, AbstractController controller) {
        setBuilderBuildObserver(controller);
        setBuilderMoveObserver(controller);
        setColorChoiceObserver(controller);
        setNewPlayerObserver(controller);
        setNumberOfPlayersObserver((controller));
        setStepChoiceObserver(controller);
        setBuilderSetupObserver(controller);
        setGodCardChoiceObserver(controller);
        setStartPlayerObserver(controller);

        this.socket = socket;
    }

    public void setConnectionObserver(ConnectionObserver obs) {
        connectionObserver = obs;
    }

    protected void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    /**
     * it flags a Virtual View as registered: nickname + date submisssion has been accepted.
     */
    public void register() {
        registered = true;
    }

    public boolean notRegistered() {
        return !registered;
    }

    /**
     * Main VirtualView function with socket read loop
     */
    @Override
    public void run() {
        try {
            int timeout = 10 * 1000;
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        try {
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        notifyConnection();



        scheduler = Executors.newSingleThreadScheduledExecutor();

        //first ping sent
        scheduler.schedule(() -> out.println(Messages.ping()), pingDelay, TimeUnit.SECONDS);

        String message;

        connected = true;

        while(connected) {
            try {
                if ((message = in.readLine()) == null) {
                    System.out.println(socket.getRemoteSocketAddress() + ": null read");
                    break;
                }
            } catch (IOException e) {
                System.err.println(socket.getRemoteSocketAddress() + ": " + e.getMessage());
                break;
            }
            if(!message.equals(Messages.pong()))
                System.out.println(socket.getRemoteSocketAddress() + ": " + message);

            handleMessage(message);

        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(socket.getRemoteSocketAddress() + " disconnected.");


        if(nickname != null)
            notifyDisconnection(nickname);
        else
            notifyEarlyDisconnection(this); //notifies disconnection when view has still not been registered

    }

    public void disconnect(){
        connected = false;
    }

    public void send(String message){
        if(!socket.isClosed()) {
            if (!message.equals(Messages.ping()))
                System.out.println("Sending to " + socket.getRemoteSocketAddress() + " : " + message);
            out.println(message);
        }
    }

    /**
     * Parses Message from client and notifies Controller according to message data.
     * @param message String read from socket. Supposed to be a JSON String
     */
    private void handleMessage(String message) {
        NetworkParser parser;

        Coordinates src, dst;
        Coordinates builder1, builder2;
        String date, color;
        int numberOfPlayers;
        String request = "";

        try {
            parser = new NetworkParser(message);
        } catch (JSONException e) {
            send(Messages.errorMessage("You did not sent a JSON String"));
            return;
        }

        try {
            request = parser.getRequest();
        } catch (JSONException e) {
            send(Messages.errorMessage("You did not specify a type of message"));
        }

        switch (request) {

            case Messages.ADD_PLAYER:
                if (this.nickname == null) {
                    try {
                        date = parser.getDate();
                        setNickname(parser.getName());
                        notifyNewPlayer(this.nickname, date);
                    } catch (JSONException e) {
                        send(Messages.errorMessage("Parsing error: " + e.getMessage()));
                    }
                } else {    //drops redundant add player command
                    send(Messages.errorMessage("Nickname already set"));
                }
                break;


            case Messages.SET_NUMBER_OF_PLAYERS:
                try {
                    numberOfPlayers = parser.getNumberOfPlayers();
                    notifyNumberOfPlayers(numberOfPlayers);
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.SET_NUMBER_OF_PLAYERS + ": " + e.getMessage()));
                }
                break;


            case Messages.COLOR_UPDATE:
                try {
                    color = parser.getColor();
                    notifyColorChoice(nickname, color);
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.COLOR_UPDATE + ": " + e.getMessage()));
                }
                break;


            case Messages.SET_GOD_CARD:
                try {
                    notifyGodCardChoice(nickname, parser.getGodCardName());
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.SET_GOD_CARD + ": " + e.getMessage()));
                }
                break;

            case Messages.SET_MATCH_GOD_CARDS:
                try {
                    notifyMatchGodCardsChoice(nickname, parser.getSetFromArray(Messages.GOD_DESCRIPTIONS));
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.SET_MATCH_GOD_CARDS + ": " + e.getMessage()));
                }
                break;

            case Messages.BUILDERS_PLACEMENT:
                try {
                    builder1 = parser.getCoordArray().get(0);
                    builder2 = parser.getCoordArray().get(1);
                    notifySetupBuilders(nickname, builder1, builder2);
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.BUILDERS_PLACEMENT + ": " + e.getMessage()));
                }
                break;


            case Messages.SET_STEP_CHOICE:
                try {
                    String stepChoice = parser.getStepChoice();
                    notifyStepChoice(nickname, stepChoice);
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.SET_STEP_CHOICE + ": " + e.getMessage()));
                }
                break;


            case Messages.SET_START_PLAYER:
                try {
                    notifySetStartPlayer(nickname, parser.getAttribute(Messages.NAME));
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.SET_START_PLAYER + ": " + e.getMessage()));
                }
                break;

            case Messages.MOVE:
                try {
                    src = parser.getSrcCoordinates();
                    dst = parser.getDstCoordinates();
                    notifyMove(nickname, src, dst);
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.MOVE + ": " + e.getMessage()));
                }
                break;

            case Messages.BUILD:
                try {
                    src = parser.getSrcCoordinates();
                    dst = parser.getDstCoordinates();
                    notifyBuild(nickname, src, dst, parser.getBuildDome());
                } catch (JSONException e) {
                    send(Messages.errorMessage(Messages.BUILD + ": " + e.getMessage()));
                }
                break;

            case Messages.DISCONNECT:
                connected = false;
                break;

            case Messages.PONG:
                scheduler.schedule(() -> send(Messages.ping()), pingDelay, TimeUnit.SECONDS); //schedules a new keep-alive message
                break;

        }
    }

    /**
     * Connection notification to connectionObserver (Controller)
     */
    public void notifyConnection() {
        connectionObserver.onConnection(this);
    }

    /**
     * Disconnection notification to connectionObserver (Controller)
     * @param player player who disconnected
     */
    protected void notifyDisconnection(String player) {
        if(connectionObserver != null)
            connectionObserver.onDisconnection(player);
        else
            System.out.println("disconnection observer is not set");
    }

    /**
     * Disconnection notification of a non registered View
     * @param view view whose socket disoconnected
     */
    protected void notifyEarlyDisconnection(VirtualView view) {
        if(connectionObserver != null)
            connectionObserver.onEarlyDisconnection(view);
        else
            System.out.println("early disconnection observer is not set");

    }

}