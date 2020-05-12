package it.polimi.ingsw.client;

import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.Model;
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
import java.util.Set;

public class NetworkHandler extends ModelObservable implements Runnable, BuilderBuildObserver, BuilderMoveObserver,
        BuilderSetupObserver, ColorChoiceObserver, GodCardChoiceObserver, NewPlayerObserver, NumberOfPlayersObserver,
        StepChoiceObserver, DisconnectionObserver {

    private Socket socket;
    private PrintWriter out;
    private View view;
    private int port;
    private String ip;

    public NetworkHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, port);



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
            ArrayList<Set<Coordinates>> list;
            String nickname;
            boolean connected = true;


            switch (parser.getRequest()) {

                //richieste Controller

                case Messages.ASK_NUMBER_OF_PLAYERS:
                    view.askNumberOfPlayers();
                    break;

                case Messages.ASK_NICK_AND_DATE:
                    view.askNickAndDate();
                    break;

                case Messages.ASK_COLOR:
                    view.askBuilderColor(parser.getSetFromArray(Messages.CHOSEN_COLORS));
                    break;

                case Messages.ASK_GOD:
                    view.askGodCard(parser.getGodDescriptions(), parser.getSetFromArray(Messages.CHOSEN_GOD_CARDS)); //TODO passare la lista dei god possibili? (specifica di gioco: il primo deve scegliere i 3 god possibili)
                    break;

                //notify dal Model

                case Messages.POSSIBLE_BUILD_DESTINATIONS:
                    list = parser.getCoordSetList();
                    notifyPossibleBuilds(list.get(0), list.get(1), list.get(2), list.get(3));
                    break;

                case Messages.POSSIBLE_MOVE_DESTINATIONS:
                    list = parser.getCoordSetList();
                    notifyPossibleMoves(list.get(0), list.get(1));
                    break;

                case Messages.BUILDERS_PLACEMENT:
                    nickname = parser.getAttribute(Messages.NAME);
                    ArrayList<Coordinates> positions = parser.getCoordArray();


                    notifyBuildersPlacement(nickname, positions.get(0), positions.get(1), parser.getResult());
                    break;

                case Messages.COLOR_UPDATE:
                    notifyColorAssigned(parser.getAttribute(Messages.NAME), parser.getAttribute(Messages.COLOR), parser.getResult());
                    break;

                case Messages.LOST_UPDATE:
                    notifyLoss(parser.getAttribute(Messages.NAME));
                    break;

                case Messages.TURN_UPDATE:
                    notifyPlayerTurn(parser.getAttribute(Messages.NAME));
                    break;

                case Messages.ENDGAME:
                    notifyEndGame(parser.getAttribute(Messages.WINNER));
                    break;

                case Messages.GOD_CARD_ASSIGNED:
                    notifyGodChoice(parser.getAttribute(Messages.NAME), parser.getAttribute(Messages.GOD_CARD), parser.getResult());
                    break;

                case Messages.PLAYER_ADDED:
                    notifyPlayerAdded(parser.getAttribute(Messages.NAME), parser.getResult());
                    break;

                case Messages.STATE_UPDATE:
                    Model.State state;
                    switch (parser.getAttribute(Messages.STATE)) {
                        case "SETUP_NUMBEROFPLAYERS":
                            state = Model.State.SETUP_NUMOFPLAYERS;
                            break;
                        case "SETUP_PLAYERS":
                            state = Model.State.SETUP_PLAYERS;
                            break;
                        case "SETUP_COLOR":
                            state = Model.State.SETUP_COLOR;
                            break;
                        case "SETUP_CARDS":
                            state = Model.State.SETUP_CARDS;
                            break;
                        case "SETUP_BUILDERS":
                            state = Model.State.SETUP_BUILDERS;
                            break;
                        case "GAME":
                            state = Model.State.GAME;
                            break;
                        case "ENDGAME":
                            state = Model.State.ENDGAME;
                            break;
                        default:
                            state = Model.State.SETUP_NUMOFPLAYERS;
                    }

                    notifyState(state);
                    break;


                case Messages.DISCONNECT:
                    connected = false;


            }

        } catch (JSONException e) {
            notifyWrongInsertion(e.getMessage());
        }
        return false;
    }


    //------------------------------------- OBSERVER METHODS -----------------------------------------------------------
/*
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
    }*/

    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean buildDome) {
        send(Messages.build(nickname, src, dst, buildDome));
    }

    @Override
    public void onBuilderMove(String nickname, Coordinates src, Coordinates dst) {
        send(Messages.move(nickname,src, dst));
    }

    @Override
    public void onBuilderSetup(String nickname, Coordinates pos1, Coordinates pos2) {
        send(Messages.buildersPlacement(nickname, pos1, pos2));
    }

    @Override
    public void onColorChoice(String nickname, String chosenColor) {
        send(Messages.colorUpdate(chosenColor));
    }

    @Override
    public void onGodCardChoice(String nickname, String godCardName) {
        send(Messages.setGodCard(godCardName));
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
    public void onStepChoice(String nickname, String step) {
        send(Messages.stepChoice(nickname, step));
    }

    @Override
    public void onDisconnection(String nickname) {
        send(Messages.disconnect());

    }
}
