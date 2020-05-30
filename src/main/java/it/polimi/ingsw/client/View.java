package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class View extends ViewObservable implements BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
        ColorAssignmentObserver, ErrorsObserver, BuildersPlacedObserver, PlayerLoseObserver, EndGameObserver,
        BuilderBuiltObserver, BuilderMovementObserver, GodChoiceObserver, PlayerAddedObserver, PlayerTurnObserver, StateObserver, ChosenStepObserver, StartPlayerSetObserver{

    public enum ViewState {
        CONNECTION, WAITING, NUMPLAYERS, NICKDATE, MATCHGODS, PLAYERGOD, STARTPLAYER, BUILDERCOLOR, BUILDERPLACEMENT, STEP, MOVE, BUILD
    }

    public static final String red = Color.ANSI_RED.escape();
    private final String yellow = Color.ANSI_YELLOW.escape();


    private ViewState state;

    //protected final Set<String> chosenColors = new HashSet<>();
    protected static final Map<String,String> chosenColorsForPlayer = new HashMap<>();

    protected Coordinates currentTurnBuilderPos;

    protected Set<Coordinates> possibleDstBuilder1;
    protected Set<Coordinates> possibleDstBuilder2;
    protected Set<Coordinates> possibleDstBuilder1forDome;
    protected Set<Coordinates> possibleDstBuilder2forDome;

    private final Set<String> matchGodCards = new HashSet<>();
    private final Map<String, String> chosenGodCardsForPlayer = new HashMap<>();


    private final ArrayList<Coordinates> chosenBuilderPositions = new ArrayList<>();

    protected GameMap gameMap;

    private String nickname;
    private String date;

    private int numberOfPlayers;

    private int chosenBuilderNum = 0;

    public void setChosenBuilderNum(int number) {
        this.chosenBuilderNum = number;
    }
    public int getChosenBuilderNum() {return this.chosenBuilderNum;}

    public abstract void run();

    protected void setNickname(String nickname) {this.nickname = nickname;}
    protected String getNickname() {return this.nickname;}

    protected void setDate(String date) {this.date = date;}
    protected String getDate() { return this.date;}

    protected synchronized void setState(View.ViewState state) {this.state = state;}
    protected View.ViewState getState() {return this.state;}

    protected void setNumberOfPlayers(int numberOfPlayers) {this.numberOfPlayers = numberOfPlayers; }
    protected int getNumberOfPlayers() { return this.numberOfPlayers;}

    protected void addMatchGodCard(String godCard) throws Exception{
        if(!matchGodCards.contains(godCard))
            matchGodCards.add(godCard);
        else
            throw new Exception("GodCard already chosen!");
    }
    protected Set<String> getMatchGodCards() { return Set.copyOf(matchGodCards);}

    protected void addBuilderPosition(Coordinates coord) {
        chosenBuilderPositions.add(coord);
    }
    protected ArrayList<Coordinates> getChosenBuilderPositions() {
        return chosenBuilderPositions;
    }

    public static String getColor(String nickname) {
        return chosenColorsForPlayer.get(nickname);
    }

    public Map<String, String> getChosenGodCardsForPlayer() { return chosenGodCardsForPlayer; }
    public static Map<String, String> getChosenColorsForPlayer() {return chosenColorsForPlayer;}


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

    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        if(result)
            gameMap.modifyHeight(dst, dome);
    }

    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        if(result) {
            gameMap.updateOccupiedCells(nickname, src, dst);

            if(getNickname().equals(nickname))
                currentTurnBuilderPos = dst;
        }

    }

    @Override
    public void onBuilderPushed(String nickname, Coordinates src, Coordinates dst) {
        gameMap.updateOccupiedCells(nickname, src, dst);
    }

    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        this.possibleDstBuilder1forDome = possibleDstBuilder1forDome;
        this.possibleDstBuilder2forDome = possibleDstBuilder2forDome;
        setState(ViewState.BUILD);
    }

    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        setState(ViewState.MOVE);
        //logger.askForAction(getState());
    }

    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        if(result)
            gameMap.setOccupiedCells(nickname, positionBuilder1, positionBuilder2);

    }

    @Override
    public void onChosenStep(String nickname, String step, boolean result) {


    }

    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        if(result)
            chosenColorsForPlayer.put(nickname, color);

    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        ;//logger.log("Player " + winnerNickname + " wins!!\n");
    }

    @Override
    public void onWrongInsertionUpdate(String error) {
    }

    @Override
    public void onWrongNumberInsertion() {
    }

    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        if(result) {
            chosenGodCardsForPlayer.put(nickname, card);
        }
    }

    @Override
    public void onMatchGodCardsAssigned(String nickname, Set<String> godCardsToUse, boolean result) {

    }

    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        if(!result)
            this.nickname = null;
        this.date = null;

    }

    @Override
    public void onLossUpdate(String nickname) {
        ;//logger.log(nickname + " lost!\n");

    }

    @Override
    public void onPlayerTurn(String nickname) {
        currentTurnBuilderPos = null;
        chosenBuilderNum = 0;
    }

    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        /*if(result)
            System.out.println("The starting player is" + nickname);
        else
            System.out.println(red + "\nERROR:"+ Color.RESET + " could not set starting player.");*/
    }

    @Override
    public void onStateUpdate(Model.State currState) {

    }
}
