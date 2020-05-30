package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.AbstractController;
import it.polimi.ingsw.server.controller.ConnectionObserver;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.network.Messages;
import it.polimi.ingsw.network.NetworkParser;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author thomas
 * VirtualView class notifies Controller (as an Observable) and is notified by Model (as an observer).
 * Each VirtualVieww is a Runnable object associated to a socket.
 */
public class VirtualView extends ViewObservable implements Runnable {

    private final int timeout = 5 * 1000;
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
        setDisconnectionObserver(controller);
        setGodCardChoiceObserver(controller);
        setStartPlayerObserver(controller);


        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            //in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            this.socket.setSoTimeout(timeout);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

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
            boolean connected = socket.isConnected();
            notifyConnection(this);
            String message;

            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> send(Messages.ping()), pingDelay, TimeUnit.SECONDS);


            while((message = in.readLine()) != null && connected) {

                System.out.println("Received message from " + socket.getRemoteSocketAddress() + ": " + message);

                connected = handleMessage(message);
            }

            disconnect();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public synchronized void send(String message) {
        System.out.println("Sending to " + socket.getRemoteSocketAddress() + " : " + message);
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

                case Messages.ADD_PLAYER:
                    //drops redundant add player command
                    if(this.nickname == null) {
                        try {
                            date = parser.getDate();
                            setNickname(parser.getName());
                            notifyNewPlayer(this.nickname, date);
                        } catch (JSONException e) {
                            send(Messages.errorMessage("Wrong nickname and date format"));
                            send(Messages.parseErrorPlayer());
                        }
                    } else {
                        send(Messages.errorMessage("nickname already set"));
                    }
                    break;


                case Messages.SET_NUMBER_OF_PLAYERS:
                    try {
                        numberOfplayers = parser.getNumberOfPlayers();
                        notifyNumberOfPlayers(numberOfplayers);
                    }
                    catch (JSONException numberException) {
                        send(Messages.errorMessage("Wrong format, be sure to insert coordinates as ints"));
                        send(Messages.parseErrorNumber());
                    }
                    break;


                case Messages.COLOR_UPDATE:
                    try {
                        color = parser.getColor();
                        notifyColorChoice(nickname, color);
                    } catch (JSONException colorException){
                        send(Messages.errorMessage("Error in stream, couldn't get color parameter"));
                        send(Messages.parseErrorColor());
                    }
                    break;


                case Messages.SET_GOD_CARD:
                    try {
                        notifyGodCardChoice(nickname, parser.getGodCardName());
                    } catch (JSONException godNameException){
                        send(Messages.errorMessage("Error in stream, couldn't get godCard name"));
                        send(Messages.parseErrorGod());
                    }
                    break;

                case Messages.SET_MATCH_GOD_CARDS:
                    notifyMatchGodCardsChoice(nickname, parser.getSetFromArray(Messages.GOD_DESCRIPTIONS));
                    break;

                case Messages.BUILDERS_PLACEMENT:
                    try {
                        builder1 = parser.getCoordArray().get(0);
                        builder2 = parser.getCoordArray().get(1);
                        notifySetupBuilders(nickname, builder1, builder2);
                    } catch (JSONException coordinatesException){
                        send(Messages.errorMessage(coordinatesException.getMessage()));
                        send(Messages.parseErrorBuilders());
                    }
                    break;


                case Messages.SET_STEP_CHOICE:
                    try{
                        String stepChoice = parser.getStepChoice();
                        notifyStepChoice(nickname, stepChoice);
                    } catch (JSONException stepException){
                        send(Messages.errorMessage("Invalid format, be sure to insert coordinates as ints"));
                        send(Messages.parseErrorStepChoice());
                    }
                    break;


                case Messages.SET_START_PLAYER:
                    //TODO try catch mancanti
                    notifySetStartPlayer(nickname, parser.getAttribute(Messages.NAME));
                    break;

                case Messages.MOVE:
                    try {
                        src = parser.getSrcCoordinates();
                        dst = parser.getDstCoordinates();
                        notifyMove(nickname, src, dst);
                    } catch ( JSONException coordinatesException){
                        send(Messages.parseErrorMove());
                    }

                    break;

                case Messages.BUILD:
                    try{
                        src = parser.getSrcCoordinates();
                        dst = parser.getDstCoordinates();
                        notifyBuild(nickname, src, dst, parser.getBuildDome());
                    } catch (JSONException e) {
                        send(Messages.parseErrorBuild());
                    }
                    break;

                case Messages.DISCONNECT:
                    connected = false;
                    notifyDisconnection(nickname);
                    send(Messages.disconnect());
                    break;

                case Messages.PONG:
                    scheduler.schedule(() -> send(Messages.ping()), pingDelay, TimeUnit.SECONDS);
                    break;

            }
        } catch (JSONException e) {
            send(Messages.errorMessage(e.getMessage()));
        }

        return connected;
    }

    /**
     * Connection notification to connectionObserver (Controller)
     */
    public void notifyConnection(VirtualView view) throws IOException{
        connectionObserver.onConnection(this);
    }


}
