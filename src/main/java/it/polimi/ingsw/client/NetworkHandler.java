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

/**
 * Manages socket connection with server.
 * It hides network layer between View and Server Controller.
 */
public class NetworkHandler extends ModelObservable implements Runnable, ConnectionObserver, BuilderBuildObserver, BuilderMoveObserver,
        BuilderSetupObserver, ColorChoiceObserver, GodCardChoiceObserver, NewPlayerObserver, NumberOfPlayersObserver,
        StepChoiceObserver, StartPlayerObserver {

    private Socket socket;
    private PrintWriter out;
    private View view;
    private int port;
    private static final int defaultPort = 2033;
    private static final String defaultIp = "localhost";
    private String ip;

    private final ExecutorService executorS; //creates thread to send notifications to View

    private SocketObserver socketObserver;
    private OpponentDisconnectionObserver opponentDisconnectionObserver;


    public NetworkHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;

        this.executorS = Executors.newCachedThreadPool();
    }

    public NetworkHandler() {
        this(defaultIp, defaultPort);
    }

    public void setIp(String ip) {

        this.ip = ip;

    }

    public void setPort(int port) {
        this.port = port;

    }

    public void setView(View view) {
        this.view = view;
    }

    public void setSocketObserver(SocketObserver o) {
        socketObserver = o;
    }

    public void setOpponentDisconnectionObserver(OpponentDisconnectionObserver o) {
        opponentDisconnectionObserver = o;
    }

    /**
     * run function for the Network Manager thread.
     * It creates the socket and connects it to the Game Server.
     */
    @Override
    public void run() {
        try {
            socket = new Socket();

            try {
                //Read timeout
                int timeout = 10 * 1000;
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

            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            } catch (IOException e) {
                notifyConnectionError(e.getMessage());
                return;
            }

            String message;

            try {
                while (connected && (message = in.readLine()) != null) {
                    connected = handleMessage(message);

                }
            } catch (SocketTimeoutException e) {
                notifyConnectionError(e.getMessage());

            } finally {

                socket.close();

                notifyDisconnection();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message) {
        if(socket != null && socket.isConnected())
            out.println(message);
    }

    /**
     * parses message received from network and notifies View
     * @param message String received from network
     * @return true if it was not a Disconnect message
     */
    private boolean handleMessage(String message) {
        boolean connected = true;

        try {
            NetworkParser parser = new NetworkParser(message);
            ArrayList<Set<Coordinates>> list;
            String nickname;



            switch (parser.getRequest()) {

                //-------------------------------CONTROLLER REQUESTS---------------------------------------------------

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

                //-------------------------------MODEL NOTIFICATIONS---------------------------------------------------

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

                case Messages.SET_MATCH_GOD_CARDS:
                    notifyMatchGodCards(parser.getSetFromArray(Messages.GOD_DESCRIPTIONS), parser.getResult());
                    break;

                case Messages.GOD_CARD_ASSIGNED:
                    notifyGodChoice(parser.getAttribute(Messages.NAME), parser.getAttribute(Messages.GOD_CARD), parser.getResult());
                    break;

                case Messages.PLAYER_ADDED:
                    notifyPlayerAdded(parser.getAttribute(Messages.NAME), parser.getResult());
                    break;

                case Messages.SET_START_PLAYER:
                    notifyStartPlayerSet(parser.getAttribute(Messages.NAME), parser.getResult());
                    break;

                case Messages.STATE_UPDATE:
                    Model.State state = Model.State.valueOf(parser.getAttribute(Messages.STATE));

                    notifyState(state);
                    break;

                //------------------------------------------------------------------------------------------------------


                case Messages.PLAYER_DISCONNECTED:
                    notifyOpponentDisconnection(parser.getAttribute(Messages.NAME));
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

                //Keep alive answer
                case Messages.PING:
                    send(Messages.pong());
                    break;


            }

        } catch (JSONException e) {
            notifyWrongInsertion(e.getMessage());
        }
        return connected;
    }

    //-------------------------------------------NETWORK OUTPUT MESSAGES------------------------------------------------

    /**
     * Sends build request over the network
     * @param nickname player that owns the builder at src position
     * @param src position of the builder that wants to build
     * @param dst where to build
     * @param buildDome true if it is a build dome request
     */
    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean buildDome) {
        send(Messages.build(nickname, src, dst, buildDome));
    }

    /**
     * Sends move request over the network
     * @param nickname player that owns the builder at src position
     * @param src position from which the builder moves
     * @param dst where the builder tries to move
     */
    @Override
    public void onBuilderMove(String nickname, Coordinates src, Coordinates dst) {
        send(Messages.move(nickname,src, dst));
    }

    /**
     * Sends builder position setup request over the network
     * @param nickname player that wants to place his builders
     * @param pos1 position for builder 1
     * @param pos2 position for builder 2
     */
    @Override
    public void onBuilderSetup(String nickname, Coordinates pos1, Coordinates pos2) {
        send(Messages.buildersPlacement(nickname, pos1, pos2));
    }

    /**
     * Sends color setup request over the network
     * @param nickname player that wants to set his color
     * @param chosenColor color chosen
     */
    @Override
    public void onColorChoice(String nickname, String chosenColor) {
        send(Messages.colorUpdate(chosenColor));
    }

    /**
     * Sends God Card choice over the network
     * @param nickname player that wants to set his card
     * @param godCardName card's name
     */
    @Override
    public void onGodCardChoice(String nickname, String godCardName) {
        send(Messages.setGodCard(godCardName));
    }

    /**
     * Sends God Cards choices over the network
     * @param nickname player that wants to set match cards
     * @param godNames cards chosen
     */
    @Override
    public void onMatchGodCardsChoice(String nickname, Set<String> godNames) {
        send(Messages.setGodCardsToUse(nickname, godNames));
    }

    /**
     * Sends Nickname and Birth date registration request over the network
     * @param nickname nick
     * @param birthday birthDate
     */
    @Override
    public void onNicknameAndDateInsertion(String nickname, String birthday) {
        send(Messages.addPlayer(nickname, birthday));
    }

    /**
     * Sends number of players setup request over the network
     * @param num number of players for the game
     */
    @Override
    public void onNumberInsertion(int num) {
        send(Messages.setNumberOfPlayers(num));
    }

    /**
     * Sends Step choice over the network
     * @param nickname player that wants to set his step choice
     * @param step (MOVE/BUILD/END)
     */
    @Override
    public void onStepChoice(String nickname, String step) {
        send(Messages.stepChoice(nickname, step));
    }

    /**
     * Notifies imminent client disconnection to the server
     */
    @Override
    public void onDisconnection() {

        send(Messages.disconnect());
    }

    /**
     * Sends starting player choice over the networks
     * @param nickname player choosing starting player
     * @param startPlayer nickname of the proposed starting player
     */
    @Override
    public void onSetStartPlayer(String nickname, String startPlayer) {
        send(Messages.setStartPlayer(startPlayer));
    }


    /**
     * connects to server
     * @param ip server ip/domain
     * @param port server port
     */
    @Override
    public void onConnection(String ip, int port) {
        setIp(ip);
        setPort(port);
        onConnection();
    }

    /**
     * connects to server
     */
    @Override
    public void onConnection() {
        executorS.execute(this);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void notifyConnectionError(String message) {
        socketObserver.onConnectionError(message);
    }

    public void notifyDisconnection() {
        socketObserver.onDisconnection();
    }

    public void notifyOpponentDisconnection(String nickname) {
        opponentDisconnectionObserver.onOpponentDisconnection(nickname);
    }
}
