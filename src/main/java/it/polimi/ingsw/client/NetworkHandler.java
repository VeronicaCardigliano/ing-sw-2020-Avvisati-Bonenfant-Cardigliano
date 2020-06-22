package it.polimi.ingsw.client;

import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.ModelObservable;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.network.Messages;
import it.polimi.ingsw.network.NetworkParser;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkHandler extends ModelObservable implements Runnable, ConnectionObserver, BuilderBuildObserver, BuilderMoveObserver,
        BuilderSetupObserver, ColorChoiceObserver, GodCardChoiceObserver, NewPlayerObserver, NumberOfPlayersObserver,
        StepChoiceObserver, StartPlayerObserver {

    private final int timeout = 5 * 1000;
    private PrintWriter out;
    private View view;
    private int port;
    private static final int defaultPort = 2033;
    private static final String defaultIp = "localhost";
    private String ip;

    private ExecutorService executorS;

    private SocketObserver socketObserver;


    public NetworkHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;

        this.executorS = Executors.newCachedThreadPool();
    }

    public NetworkHandler() {
        this(defaultIp, defaultPort);
    }

    public boolean setIp(String ip) {
        boolean result = true;

        if(ip != null)
            this.ip = ip;
        else
            result = false;

        return result;
    }

    public boolean setPort(int port) {
        boolean result = true;
        int minimumPort = 1025;

        if(port >= minimumPort)
            this.port = port;
        else
            result = false;

        return result;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setSocketObserver(SocketObserver o) {
        socketObserver = o;
    }

    /**
     * run function for the Network Manager thread.
     * It creates the socket and connects it to the Game Server.
     */
    @Override
    public void run() {
        try {
            Socket socket = new Socket();

            try {
                socket.connect(new InetSocketAddress(ip, port), timeout);
                socket.setSoTimeout(timeout);
                socket.setKeepAlive(true);
            } catch (SocketTimeoutException | ConnectException | IllegalArgumentException | NoRouteToHostException e) {
                notifyConnectionError(e.getMessage());
                return;
            } catch (UnknownHostException e) {
                notifyConnectionError("Unknown Host: " + e.getMessage());
                return;
            }



            boolean connected = socket.isConnected();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);


            String message;

            try {
                while ((message = in.readLine()) != null && connected) {
                    if (!Messages.ping().equals(message))
                        ;//System.out.println("Received message from " + socket.getRemoteSocketAddress() + ": " + message);

                    connected = handleMessage(message);

                }
            } catch (SocketTimeoutException e) {
                notifyConnectionError(e.getMessage());

            } finally {

                in.close();
                out.close();
                socket.close();

                notifyDisconnection();
            }

        }
        catch (IOException e) {
            //System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void send(String message) {

        if(!Messages.pong().equals(message))
            ;//System.out.println("Sending: " + message);
        out.println(message);
    }


    private boolean handleMessage(String message) {
        boolean connected = true;

        try {
            NetworkParser parser = new NetworkParser(message);
            ArrayList<Set<Coordinates>> list;
            String nickname;



            switch (parser.getRequest()) {

                //richieste Controller



                case Messages.ASK_NUMBER_OF_PLAYERS:
                    executorS.execute(() -> view.askNumberOfPlayers());
                    //view.askNumberOfPlayers();
                    break;

                case Messages.ASK_NICK_AND_DATE:
                    //executorS.execute(() -> view.askNickAndDate());
                    view.askNickAndDate();
                    break;

                case Messages.ASK_COLOR:
                    executorS.execute(() -> view.askBuilderColor(parser.getSetFromArray(Messages.CHOSEN_COLORS)));
                    //view.askBuilderColor(parser.getSetFromArray(Messages.CHOSEN_COLORS));
                    break;

                case Messages.ASK_GOD:
                    executorS.execute(() -> view.askGodCard(parser.getGodDescriptions(), parser.getSetFromArray(Messages.CHOSEN_GOD_CARDS)));
                    //view.askGodCard(parser.getGodDescriptions(), parser.getSetFromArray(Messages.CHOSEN_GOD_CARDS));
                    break;

                case Messages.CHOOSE_MATCH_GOD_CARDS:
                    executorS.execute(() -> view.chooseMatchGodCards(parser.getNumberOfPlayers(), parser.getGodDescriptions()));
                    //view.chooseMatchGodCards(parser.getNumberOfPlayers(), parser.getGodDescriptions());
                    break;

                case Messages.ASK_BUILDERS:
                    executorS.execute(() -> view.placeBuilders());
                    //view.placeBuilders();
                    break;

                case Messages.ASK_STEP:
                    executorS.execute(() -> view.chooseNextStep(parser.getSetFromArray(Messages.POSSIBLE_STEPS)));
                    //view.chooseNextStep(parser.getSetFromArray(Messages.POSSIBLE_STEPS));
                    break;

                case Messages.CHOOSE_START_PLAYER:
                    executorS.execute(() -> view.chooseStartPlayer(parser.getSetFromArray(Messages.PLAYERS)));
                    //view.chooseStartPlayer(parser.getSetFromArray(Messages.PLAYERS));
                    break;
                //notify dal Model

                case Messages.MOVE:
                    notifyBuilderMovement(parser.getAttribute(Messages.NAME), parser.getSrcCoordinates(), parser.getDstCoordinates(), parser.getResult());
                    break;

                case Messages.BUILDER_PUSHED:
                    notifyBuilderPushed(parser.getAttribute(Messages.NAME), parser.getSrcCoordinates(), parser.getDstCoordinates());
                    break;

                case Messages.BUILD:
                    notifyBuilderBuild(parser.getAttribute(Messages.NAME), parser.getSrcCoordinates(), parser.getDstCoordinates(), parser.getBuildDome(), parser.getResult());
                    break;

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
                    Model.State state = Model.State.valueOf(parser.getAttribute(Messages.STATE));

                    notifyState(state);
                    break;


                case Messages.ERROR_NUMBER:
                    notifyWrongNumber();
                    break;

                case Messages.ERROR:
                    notifyWrongInsertion(parser.getAttribute(Messages.DESCRIPTION));
                    break;

                case Messages.DISCONNECT:
                    connected = false;
                    break;

                case Messages.PING:
                    send(Messages.pong());
                    break;


            }

        } catch (JSONException e) {
            notifyWrongInsertion(e.getMessage());
        }
        return connected;
    }

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
    public void onMatchGodCardsChoice(String nickname, Set<String> godNames) {
        send(Messages.setGodCardsToUse(nickname, godNames));
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
    public void onDisconnection() {
        send(Messages.disconnect());


    }

    @Override
    public void onSetStartPlayer(String nickname, String startPlayer) {
        send(Messages.setStartPlayer(startPlayer));
    }


    @Override
    public void onConnection(String ip, int port) {
        setIp(ip);
        setPort(port);

        onConnection();
    }

    @Override
    public void onConnection() {
        executorS.execute(this);
    }

    public void notifyConnectionError(String message) {
        socketObserver.onConnectionError(message);
    }

    public void notifyDisconnection() {
        socketObserver.onDisconnection();
    }
}
