package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.*;

import java.util.*;

/**
 * Abstract Class implemented by UI.
 *
 */
public abstract class View extends ViewObservable implements BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
        ColorAssignmentObserver, ErrorsObserver, BuildersPlacedObserver, PlayerLoseObserver, EndGameObserver,
        BuilderBuiltObserver, BuilderMovementObserver, GodChoiceObserver, PlayerAddedObserver, PlayerTurnObserver, StateObserver, ChosenStepObserver, StartPlayerSetObserver,
        SocketObserver, OpponentDisconnectionObserver {

    public enum ViewState {
        CONNECTION, WAITING, NUMPLAYERS, NICKDATE, MATCHGODS, PLAYERGOD, STARTPLAYER, BUILDERCOLOR, BUILDERPLACEMENT, STEP, MOVE, BUILD, END
    }

    private ConnectionObserver connectionObserver; //will connect to server when View sends a notifyConnection

    private ViewState state;

    protected static final Map<String,String> chosenColorsForPlayer = new HashMap<>();

    protected Coordinates currentTurnBuilderPos; //position of the chosen builder during turn

    protected Set<Coordinates> possibleDstBuilder1 = new HashSet<>();       //positions at which builder 1 can move or build
    protected Set<Coordinates> possibleDstBuilder2 = new HashSet<>();       //positions at which builder 2 can move or build
    protected Set<Coordinates> possibleDstBuilder1forDome = new HashSet<>();//positions at which builder 1 can build a dome
    protected Set<Coordinates> possibleDstBuilder2forDome = new HashSet<>();//positions at which builder 2 can build a dome

    private Map<String, String> chosenGodCardsForPlayer = new HashMap<>();
    //private Map<String,String> matchGodCards = new HashMap<>();

    protected GameMap gameMap;
    private String nickname;
    private String date;

    private int numberOfPlayers;

    public View () {
        setState(ViewState.CONNECTION);
    }

    /**
     * Main User Interface Function.
     */
    public abstract void run();

    protected void setNickname(String nickname) {this.nickname = nickname;}
    protected String getNickname() {return this.nickname;}

    protected void setDate(String date) {this.date = date;}
    protected String getDate() { return this.date;}

    protected synchronized void setState(View.ViewState state) {this.state = state;}
    protected View.ViewState getState() {return this.state;}

    protected void setNumberOfPlayers(int numberOfPlayers) {this.numberOfPlayers = numberOfPlayers; }
    protected int getNumberOfPlayers() { return this.numberOfPlayers;}

    public static String getColor(String nickname) {
        return chosenColorsForPlayer.get(nickname);
    }

    public Map<String, String> getChosenGodCardsForPlayer() { return chosenGodCardsForPlayer; }
    public static Map<String, String> getChosenColorsForPlayer() {return chosenColorsForPlayer;}


    //-------------------------------------INPUT REQUESTS--------------------------------------------------------------

    public void askNumberOfPlayers () {
        setState(ViewState.NUMPLAYERS);
    }
    public void askNickAndDate () {

        setState(ViewState.NICKDATE);
    }

    public void chooseMatchGodCards(int numOfPlayers, Map<String, String> godDescriptionsParam) {
        this.numberOfPlayers = numOfPlayers;

        setState(ViewState.MATCHGODS);
    }

    public void askGodCard (Map<String, String> godDescriptions, Set<String> chosenCards) {
        setState(ViewState.PLAYERGOD);

    }

    public void chooseStartPlayer (Set<String> players) {
        setState(ViewState.STARTPLAYER);
    }
    public void askBuilderColor (Set<String> chosenColors) {
        setState(ViewState.BUILDERCOLOR);
    }
    public void placeBuilders () {
        setState(ViewState.BUILDERPLACEMENT);
    }

    public void chooseNextStep (Set<String> possibleSteps) {
        setState(ViewState.STEP);
    }

    //-----------------------------------------------------------------------------------------------------------------

    public void setConnectionObserver(ConnectionObserver observer) {
        this.connectionObserver = observer;
    }

    public void notifyConnection(String ip, int port) {
        connectionObserver.onConnection(ip, port);
    }
    public void notifyDisconnection() {connectionObserver.onDisconnection();}

    //build and move functions overridden by each specific UI
    abstract public void build();
    abstract public void move();



    //----------------------------------------EVENT HANDLERS------------------------------------------------------------


    /**
     * Updates GameMap object after a Build event if the result is true
     * @param nickname player who played
     * @param src position of the builder who built
     * @param dst where the builder built
     * @param dome true if the builder built a Dome
     * @param result true if the server accepted it
     */
    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        possibleDstBuilder1forDome.clear();
        possibleDstBuilder2forDome.clear();
    }

    /**
     * Updates GameMap object after a Move event if the result is true
     * @param nickname player who played
     * @param src position of the builder that moved
     * @param dst where the builder moved
     * @param result true if the server accepted it
     */
    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        if(result) {
            gameMap.updateOccupiedCells(nickname, src, dst);

            if(getNickname().equals(nickname))
                currentTurnBuilderPos = dst;
        }

    }

    /**
     * Updates GameMap object after a Push event
     * @param nickname nickname of the player whose builder was pushed
     * @param src position of the builder pushed
     * @param dst where the builder was pushed
     */
    @Override
    public void onBuilderPushed(String nickname, Coordinates src, Coordinates dst) {
        gameMap.updateOccupiedCells(nickname, src, dst);
    }

    /**
     * Updates the positions where builders can build
     * @param possibleDstBuilder1 positions where builder 1 can build a normal building
     * @param possibleDstBuilder2 positions where builder 2 can build a normal building
     * @param possibleDstBuilder1forDome positions where builder 1 can build a dome
     * @param possibleDstBuilder2forDome positions where builder 2 can build a dome
     */
    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        this.possibleDstBuilder1forDome = possibleDstBuilder1forDome;
        this.possibleDstBuilder2forDome = possibleDstBuilder2forDome;
        setState(ViewState.BUILD);
    }

    /**
     * Updates the positions at which builders can move
     * @param possibleDstBuilder1 positions at which builder 1 can move
     * @param possibleDstBuilder2 positions at which builder 2 can move
     */
    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        this.possibleDstBuilder1forDome = new HashSet<>();
        this.possibleDstBuilder2forDome = new HashSet<>();

        setState(ViewState.MOVE);
        //logger.askForAction(getState());
    }

    /**
     * Updates GameMap object with new builders if the result is true
     * @param nickname nickname of the player who placed his builders
     * @param positionBuilder1 position of the first builder placed
     * @param positionBuilder2 position of the second builder placed
     * @param result true if the server accepted it
     */
    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        if(result)
            gameMap.setOccupiedCells(nickname, positionBuilder1, positionBuilder2);
        else

            setState(ViewState.BUILDERPLACEMENT);
    }

    /**
     * Handles server response to a Step choice request
     * @param nickname nickname of the player who chose the step
     * @param step step chosen
     * @param result true if the server accepted the request
     */
    @Override
    public void onChosenStep(String nickname, String step, boolean result) {
    }

    /**
     * Handles server response to a color assignment request
     * @param nickname nickname of the player who chose his color
     * @param color color chosen
     * @param result true if the server accepted the request
     */
    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        if(result)
            chosenColorsForPlayer.put(nickname, color);
        else
            setState(ViewState.BUILDERCOLOR);
    }

    /**
     * Handles game over notification from server
     * @param winnerNickname nickname of the player who won
     */
    @Override
    public void onEndGameUpdate(String winnerNickname) {
        setState(ViewState.END);
    }

    /**
     * Handles generic error from server
     * @param error String representing the error
     */
    @Override
    public void onWrongInsertionUpdate(String error) {
    }

    /**
     * Handles error during the setting of the number of players for the game
     */
    @Override
    public void onWrongNumberInsertion() {
        setNumberOfPlayers(0);
    }

    /**
     * Handles server response to a card choice event
     * @param nickname nickname of the player who chose the card
     * @param card card chosen
     * @param result true if the server accepted the request
     */
    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        if(result) {
            chosenGodCardsForPlayer.put(nickname, card);
        }
    }

    /**
     * Handles server response to the game cards choice
     * @param godCardsToUse cards chosen
     * @param result true if the server accepted the choice
     */
    @Override
    public void onMatchGodCardsAssigned(Set<String> godCardsToUse, boolean result) {

    }

    /**
     * Handles server response to a nickname and birthDate registration
     * @param nickname nickname of the player
     * @param result true if the server accepted the registration
     */
    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        if(!result) {
            this.nickname = null;
            this.date = null;
            setState(ViewState.NICKDATE);
        }

    }

    /**
     * Handles server notification about a player loss
     * @param nickname nickname of the player who just lost
     */
    @Override
    public void onLossUpdate(String nickname) {

        chosenGodCardsForPlayer.remove(nickname);
    }

    /**
     * Handles server notification about who is now playing
     * @param nickname nickname of the player whose turn is
     */
    @Override
    public void onPlayerTurn(String nickname) {

        gameMap.setChosenBuilderNum(0);
    }

    /**
     * Handles server notification about start player chosen
     * @param nickname of the starting player
     * @param result true if correctly set
     */
    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        if(!result)
            setState(ViewState.STARTPLAYER);
    }

    /**
     * Notifies an update in Model state
     */
    @Override
    public void onStateUpdate(Model.State currState) {
    }

    /**
     * called when network handler socket is disconnected from server.
     * It changes the View state back to CONNECTION and reinitialize in-game data.
     * It however does not reset the Map so you will have to create a new GameMap object or clean it.
     */
    @Override
    public void onDisconnection() {

        setNickname(null);
        setDate(null);
        //resetting all game data
        chosenGodCardsForPlayer = new HashMap<>();
        currentTurnBuilderPos = null;
        //gameMap.setChosenBuilderNum(0);
    }

    //-----------------------------------------------------------------------------------------------------------------
}
