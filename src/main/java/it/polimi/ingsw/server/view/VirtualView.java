package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.AbstractController;
import it.polimi.ingsw.server.controller.ConnectionObserver;
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
    private Scanner in;
    private String nickname;
    private ConnectionObserver connectionObserver;

    public VirtualView(Socket socket, AbstractController controller) {
        setObservers(controller);


        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
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
            //Scanner in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            //out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            boolean connected = socket.isConnected();

            notifyConnection(this);


            while(connected) {

                String message = in.nextLine();

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

    public void send(String message) {
        System.out.println("Sending to " + socket.getRemoteSocketAddress() + " : " + message);
        out.println(message);
        //out.flush();
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
                            send(Messages.parsErrorPlayer());
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
                        send(Messages.parsErrorNumber());
                    }
                    break;


                case Messages.COLOR_UPDATE:
                    try {
                        color = parser.getColor();
                        notifyColorChoice(nickname, color);
                    } catch (JSONException colorException){
                        send(Messages.errorMessage("Error in stream, couldn't get color parameter"));
                        send(Messages.parsErrorColor());
                    }
                    break;


                case Messages.SET_GOD_CARD:
                    try {
                        notifyGodCardChoice(nickname, parser.getGodCardName());
                    } catch (JSONException godNameException){
                        send(Messages.errorMessage("Error in stream, couldn't get godCard name"));
                        send(Messages.parsErrorGod());
                    }
                    break;


                case Messages.BUILDERS_PLACEMENT:
                    try {
                    builder1 = parser.getCoordArray().get(0);
                    builder2 = parser.getCoordArray().get(1);
                    notifySetupBuilders(nickname, builder1, builder2);
                    } catch (JSONException coordinatesException){
                        send(Messages.errorMessage(coordinatesException.getMessage()));
                        send(Messages.parsErrorBuilders());
                    }
                    break;


                case Messages.SET_STEP_CHOICE:
                    try{
                    String stepChoice = parser.getStepChoice();
                    notifyStepChoice(nickname, stepChoice);
                    } catch (JSONException stepException){
                        send(Messages.errorMessage("Invalid format, be sure to insert coordinates as ints"));
                        send(Messages.parsErrorStepChoice());
                    }
                    break;


                case Messages.MOVE:
                    try {
                        src = parser.getSrcCoordinates();
                        dst = parser.getDstCoordinates();
                        notifyMove(nickname, src, dst);
                    } catch ( JSONException coordinatesException){
                        send(Messages.parsErrorMove());
                    }

                    break;

                case Messages.BUILD:
                    try{
                    src = parser.getSrcCoordinates();
                    dst = parser.getDstCoordinates();
                    notifyBuild(nickname, src, dst, parser.getBuildDome());
                    } catch (JSONException e) {
                        send(Messages.parsErrorBuild());
                    }
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

    /**
     * Connection notification to connectionObserver (Controller)
     */
    public void notifyConnection(VirtualView view) throws IOException{
        connectionObserver.onConnection(this);
    }


}
