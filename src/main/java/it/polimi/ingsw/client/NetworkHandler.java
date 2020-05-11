package it.polimi.ingsw.client;

import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.ModelObservable;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.parser.Messages;
import it.polimi.ingsw.server.parser.NetworkParser;
import org.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class NetworkHandler extends ModelObservable implements Runnable, BuilderBuildObserver, BuilderMoveObserver,
        BuilderSetupObserver, ColorChoiceObserver, GodCardChoiceObserver, NewPlayerObserver, NumberOfPlayersObserver,
        StepChoiceObserver, DisconnectionObserver {

    private final Socket socket;
    private PrintWriter out;

    public NetworkHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            boolean connected = socket.isConnected();

            while(connected) {
                String message = in.nextLine();

                //System.out.println("Received message from " + socket.getRemoteSocketAddress() + ": " + message);

                connected = handleMessage(message);


            }

            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void send(String message) {
        out.println(message);
    }


    private boolean handleMessage(String message) {
        try {
            NetworkParser parser = new NetworkParser(message);


            switch (parser.getRequest()) {

                //richieste Controller

                case Messages.ASK_NUMBER_OF_PLAYERS:
                    //abstractView.askNumberOfPlayers();
                    break;

                case Messages.ASK_NICK_AND_DATE:
                    //abstractView.askNickAndDate();
                    break;

                case Messages.ASK_COLOR:
                    //abstractView.askColor();
                    break;

                case Messages.ASK_GOD:
                    //abstractView.askGod(); //TODO passare la lista dei god possibili? (specifica di gioco: il primo deve scegliere i 3 god possibili)
                    break;

                //notify dal Model

                case Messages.POSSIBLE_BUILD_DESTINATIONS:
                    ArrayList<Coordinates> list = parser.getCoordList();
                    //notifyPossibleBuilds();

            }

        } catch (JSONException e) {

        }
        return false;
    }


    //------------------------------------- OBSERVER METHODS -----------------------------------------------------------

    @Override
    public void onBuilderBuild(String player, Coordinates src, Coordinates dst, boolean buildDome) {

    }

    @Override
    public void onBuilderMove(String player, Coordinates src, Coordinates dst) {

    }

    @Override
    public void onBuilderSetup(String player, Coordinates pos1, Coordinates pos2) {

    }

    @Override
    public void onColorChoice(String player, String chosenColor) {

    }

    @Override
    public void onGodCardChoice(String player, String godCardName) {

    }

    @Override
    public void onNicknameAndDateInsertion(String nickname, String birthday) {
        send(Messages.addPlayer(nickname, birthday));
    }

    @Override
    public void onNumberInsertion(int num) {
        send(Messages.setNumberOfPlayers(num));
    }

    @Override
    public void onStepChoice(String player, String step) {
        send(Messages.stepChoice(step));
    }

    @Override
    public void onDisconnection(String player) {
        send(Messages.disconnect());
    }
}
