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
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author thomas
 * VirtualView class notifies Controller (as an Observable) and is notified by Model (as an observer).
 * Each VirtualVieww is a Runnable object associated to a socket.
 */
public class VirtualView extends ViewObservable implements Runnable {

    private final int timeout = 10 * 1000;
    private final int pingDelay = 2;
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;
    private ConnectionObserver connectionObserver;
    private ScheduledExecutorService scheduler;

    public VirtualView(Socket socket, AbstractController controller) {
        setBuilderBuildObserver(controller);
        setBuilderMoveObserver(controller);
        setColorChoiceObserver(controller);
        setNewPlayerObserver(controller);
        setNumberOfPlayersObserver((controller));
        setStepChoiceObserver(controller);
        setBuilderSetupObserver(controller);
        //setDisconnectionObserver(controller);
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


    @Override
    public void run() {
        try {
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

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            notifyConnection(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> out.println(Messages.ping()), pingDelay, TimeUnit.SECONDS);

        String message = null;

        while(true) {
            try {
                if ((message = in.readLine()) == null) {
                    System.out.println(socket.getRemoteSocketAddress() + ": null read");
                    break;
                }
            } catch (IOException e) {
                System.err.println(socket.getRemoteSocketAddress() + ": " + e.getMessage());
                break;
            }
            System.out.println(socket.getRemoteSocketAddress() + ": " + message);
            handleMessage(message);

        }

        disconnect();

        if(nickname != null)
            notifyDisconnection(nickname);
        else
            notifyEarlyDisconnection(this);

    }

    public void disconnect(){
        try {
            System.out.println(socket.getRemoteSocketAddress() + " disconnected.");
            in.close();
            out.close();
            socket.close();
        } catch (IOException ignored) {

        }
    }

    public synchronized void send(String message){
        System.out.println("Sending to " + socket.getRemoteSocketAddress() + " : " + message);
        out.println(message);
    }

    /**
     * Parses Message from client and notifies Controller according to message data.
     * @param message String read from socket. Supposed to be a JSON String
     */
    private void handleMessage(String message) {

        try{
            NetworkParser parser = new NetworkParser(message);

            Coordinates src, dst;
            Coordinates builder1, builder2;
            String date, color;
            int numberOfPlayers;

            switch (parser.getRequest()) {

                case Messages.ADD_PLAYER:
                    //drops redundant add player command
                    if(this.nickname == null) {
                        try {
                            date = parser.getDate();
                            setNickname(parser.getName());
                            notifyNewPlayer(this.nickname, date);
                        } catch (JSONException e) {
                            send(Messages.errorMessage("Parsing error: " + e.getMessage()));
                            //send(Messages.parseErrorPlayer());
                        }
                    } else {
                        send(Messages.errorMessage("nickname already set"));
                    }
                    break;


                case Messages.SET_NUMBER_OF_PLAYERS:
                    try {
                        numberOfPlayers = parser.getNumberOfPlayers();
                        notifyNumberOfPlayers(numberOfPlayers);
                    }
                    catch (JSONException e) {
                        send(Messages.errorMessage(e.getMessage()));
                        //send(Messages.parseErrorNumber());
                    }
                    break;


                case Messages.COLOR_UPDATE:
                    try {
                        color = parser.getColor();
                        notifyColorChoice(nickname, color);
                    } catch (JSONException e){
                        send(Messages.errorMessage(e.getMessage()));
                        //send(Messages.parseErrorColor());
                    }
                    break;


                case Messages.SET_GOD_CARD:
                    try {
                        notifyGodCardChoice(nickname, parser.getGodCardName());
                    } catch (JSONException e){
                        send(Messages.errorMessage(e.getMessage()));
                        //send(Messages.parseErrorGod());
                    }
                    break;

                case Messages.SET_MATCH_GOD_CARDS:
                    try {
                        notifyMatchGodCardsChoice(nickname, parser.getSetFromArray(Messages.GOD_DESCRIPTIONS));
                    } catch (JSONException e) {
                        send(Messages.errorMessage(e.getMessage()));
                    }
                    break;

                case Messages.BUILDERS_PLACEMENT:
                    try {
                        builder1 = parser.getCoordArray().get(0);
                        builder2 = parser.getCoordArray().get(1);
                        notifySetupBuilders(nickname, builder1, builder2);
                    } catch (JSONException e){
                        send(Messages.errorMessage(e.getMessage()));
                        //send(Messages.parseErrorBuilders());
                    }
                    break;


                case Messages.SET_STEP_CHOICE:
                    try{
                        String stepChoice = parser.getStepChoice();
                        notifyStepChoice(nickname, stepChoice);
                    } catch (JSONException e){
                        send(Messages.errorMessage(e.getMessage()));
                        //send(Messages.parseErrorStepChoice());
                    }
                    break;


                case Messages.SET_START_PLAYER:
                    try {
                        notifySetStartPlayer(nickname, parser.getAttribute(Messages.NAME));
                    } catch (JSONException e) {
                        send(Messages.errorMessage(e.getMessage()));
                    }
                    break;

                case Messages.MOVE:
                    try {
                        src = parser.getSrcCoordinates();
                        dst = parser.getDstCoordinates();
                        notifyMove(nickname, src, dst);
                    } catch ( JSONException e){
                        send(Messages.errorMessage(e.getMessage()));
                        //send(Messages.parseErrorMove());
                    }

                    break;

                case Messages.BUILD:
                    try{
                        src = parser.getSrcCoordinates();
                        dst = parser.getDstCoordinates();
                        notifyBuild(nickname, src, dst, parser.getBuildDome());
                    } catch (JSONException e) {
                        send(Messages.errorMessage(e.getMessage()));
                        //send(Messages.parseErrorBuild());
                    }
                    break;

                case Messages.DISCONNECT:

                    //send(Messages.disconnect());
                    break;

                case Messages.PONG:
                    scheduler.schedule(() -> send(Messages.ping()), pingDelay, TimeUnit.SECONDS);
                    break;

            }
            //todo tenere solo questo mega try/catch oppure quelli annidati?
        } catch (JSONException e) {
            send(Messages.errorMessage(e.getMessage()));
        }
    }

    /**
     * Connection notification to connectionObserver (Controller)
     */
    public void notifyConnection(VirtualView view) throws IOException{
        connectionObserver.onConnection(this);
    }

    protected void notifyDisconnection(String player) {
        if(connectionObserver != null)
            connectionObserver.onDisconnection(player);
        else
            System.out.println("disconnection observer is not set");
    }

    protected void notifyEarlyDisconnection(VirtualView view) {
        if(connectionObserver != null)
            connectionObserver.onEarlyDisconnection(view);
        else
            System.out.println("early disconnection observer is not set");

    }

}
