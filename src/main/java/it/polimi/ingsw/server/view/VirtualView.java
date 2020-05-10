package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.parser.Messages;
import it.polimi.ingsw.server.parser.NetworkParser;
import org.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

/**
 * @author thomas
 * VirtualView class notifies Controller (as an Observable) and is notified by Model (as an observer).
 * Each VirtualVieww is a Runnable object associated to a socket.
 */
public class VirtualView extends ViewObservable implements Runnable, BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
                            ErrorsObserver, BuildersPlacementObserver, PlayerLoseObserver, EndGameObserver {

    private final Socket socket;
    private PrintWriter out;
    private String nickname;

    public VirtualView(Socket socket) {
        this.socket = socket;

    }

    private void setNickname(String nickname) {
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

                System.out.println("Received message from " + socket.getInetAddress() + ": " + message);

                connected = handleMessage(message);


            }

            socket.close();
            in.close();
            out.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void send(String message) {
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
                case "MOVE":
                    src = parser.getSrcCoordinates();
                    dst = parser.getDstCoordinates();
                    notifyMove(nickname, src, dst);
                    break;

                case "BUILD":
                    src = parser.getSrcCoordinates();
                    dst = parser.getDstCoordinates();
                    notifyBuild(nickname, src, dst, parser.getBuildDome());
                    break;

                case "SETCURRPLAYERBUILDERS":
                    builder1 = parser.getSrcCoordinates();
                    builder2 = parser.getDstCoordinates();
                    notifySetupBuilders(nickname, builder1, builder2);
                    break;

                case "ASSIGNCOLOR":
                    color = parser.getColor();
                    notifyColorChoice(nickname, color);
                    break;

                case "ADDPLAYER":
                    //drops redundant add player command
                    if(this.nickname != null) {
                        date = parser.getDate();
                        setNickname(parser.getName());

                        notifyNewPlayer(this.nickname, date);
                    } else {
                        send(Messages.errorMessage("nickname already set"));
                    }
                    break;

                case "DELETEPLAYER":
                    notifyPlayerDeletion(this.nickname);
                    break;

                case "SETNUMBEROFPLAYERS":
                    numberOfplayers = parser.getNumberOfPlayers();
                    notifyNumberOfPlayers(numberOfplayers);
                    break;

                case "STEPCHOICE":
                    String stepChoice = parser.getStepChoice();
                    notifyStepChoice(nickname, stepChoice);
                    break;

                case "DISCONNECT":
                    connected = false;

            }
        } catch (JSONException e) {
            send(Messages.errorMessage(e.getMessage()));
        }

        return connected;
    }

    //--------------------------------------- OBSERVER METHODS ---------------------------------------------------------

    @Override
    public void updatePossibleBuildDst(String nickname, Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        send(Messages.possibleBuildDestinations(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome));
    }

    @Override
    public void onWrongInsertionUpdate(String nickname, String error) {
        send(Messages.errorMessage(error));
    }

    @Override
    public void onLossUpdate(String nickname) {
        send(Messages.lostGame());
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        send(Messages.endGame(winnerNickname));
    }

    @Override
    public void onBuildersPlacementUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2) {
        send(Messages.buildersPlacement(nickname, positionBuilder1, positionBuilder2));
    }


    @Override
    public void updatePossibleMoveDst(String nickname, Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        send(Messages.possibleMoveDestinations(possibleDstBuilder1, possibleDstBuilder2));
    }
}
