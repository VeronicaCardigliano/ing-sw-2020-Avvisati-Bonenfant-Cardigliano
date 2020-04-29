package it.polimi.ingsw.model;

import it.polimi.ingsw.parser.GodCardParser;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * @author veronica
 * Model class that specifies the single match, containing the list of players, the gameMap,
 * the Set of chosen Cards and Colors and the jsonPath
 */

public class Model extends ModelObservable {
    private final static String jsonPath = "src/main/java/it/polimi/ingsw/parser/cards.json";

    private final ArrayList<Player> players = new ArrayList<>();
    private int numPlayers;
    private final IslandBoard gameMap;
    private final CyclingIterator<Player> turnManager = new CyclingIterator<>(players);

    //in this class currState refers to the match state, instead currStep refers to the single movement during GAME state
    public enum State { SETUP_NUMOFPLAYERS, SETUP_PLAYERS, SETUP_CARDS, SETUP_BUILDERS, GAME, ENDGAME }
    private State currState;
    private String currStep;
    private GodCardParser cardsParser;


    private Player currPlayer;

    Set<String> chosenCards = new HashSet<>();
    Set<String> chosenColors = new HashSet<>();

    /**
     * PossibleDst Arrays contains the list of Cells in which the player can move or build
     */
    protected ArrayList<Cell> possibleDstBuilder1;
    protected ArrayList<Cell> possibleDstBuilder2;
    protected ArrayList<Cell> possibleDstBuilder1forDome;
    protected ArrayList<Cell> possibleDstBuilder2forDome;

    /**
     * The constructor initialises the GameMap and assigns it to the GodCard as a static attribute, common to each card.
     * It also sets the currState to SETUP_PLAYERS, which is the first one, and loads the json file
     */

    public Model () {

        this.gameMap = new IslandBoard();
        this.cardsParser = new GodCardParser(jsonPath);
        this.currState = State.SETUP_NUMOFPLAYERS;
    }

    /**
     * This method is called by Controller in the end of every state to go in the next one
     */

    public void setNextState() {
        switch (currState) {
            case SETUP_NUMOFPLAYERS:
                currState = State.SETUP_PLAYERS;
                break;
            case SETUP_PLAYERS:
                currState = State.SETUP_CARDS;
                break;
            case SETUP_CARDS:
                currState = State.SETUP_BUILDERS;
                break;
            case SETUP_BUILDERS:
                currState = State.GAME;
                break;
            case GAME:
                currState = State.ENDGAME;
                break;
        }
        //notifyState(currState);
    }



    public int getNumPlayers(){
        return this.numPlayers;
    }

    public State getCurrState () {
        return this.currState;
    }

    public Player getCurrPlayer () {
        return currPlayer;
    }

    public String getCurrPlayerName() {
        return currPlayer.getNickname();
    }

    public Set<String> getGodNames() {
        return this.cardsParser.getGodDescriptions().keySet();
    }

    public Map<String, String> getGodDescriptions() {
        return this.cardsParser.getGodDescriptions();
    }

    public Set<String> getChosenCards () {
        return this.chosenCards;
    }

    public Set<String> getChosenColors () {
        return this.chosenColors;
    }

    public void setNextPlayer () {
        if (currState == State.SETUP_PLAYERS)
            currPlayer = players.get(0);
        else {
            currPlayer = turnManager.next();
            if (currState == State.GAME)
                currPlayer.startTurn();
        }
    }

    /**
     * @return returns a copy of the list of players so that external methods can't modify the ArrayList
     */

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    /**
     * deletes a player if he loses the match (if the number of players is three, the match will go on)
     * @param player who has lost the match
     * @exception IllegalArgumentException is thrown if the player isn't in the list of players of the match
     */

    public void deletePlayer (Player player) throws IllegalArgumentException {
        boolean found = players.remove(player);

        for (Builder x: player.getBuilders()) {
            x.getCell().removeOccupant();
        }

        if (!found)
            throw new IllegalArgumentException("Player not found");
    }

    public boolean setNumberOfPlayers(int num){
        if (num == 2 || num == 3) {
            numPlayers = num;
            return true;
        }
        else
            notifyWrongInsertion("ERROR: number of player not valid, choose '2' or '3'");
        return false;
    }

    /**
     * This method ensures that the players are added in order of birthday, from the youngest to the oldest
     * @param nickname: unique identifier of a new player
     * @param birthday: String that represents a birthday (yyyy.mm.dd)
     */
    //TODO: controls on correct date
    public boolean addPlayer (String nickname, String birthday) {
        boolean added = true;
        boolean younger = false;
        Player newPlayer;
        Player tmp = null;

        if (nickname == null) {
            notifyWrongInsertion("ERROR: Nickname can't be null ");
            added = false;
        }

        if(players.contains(nickname)) {
            notifyWrongInsertion("Nickname: " + nickname + " is already in use.");
            added = false;
        }


        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        long epoch = 0;

        try {
            epoch = df.parse(birthday).getTime();

            if(epoch > LocalDate.now().toEpochDay()) {
                notifyWrongInsertion("Invalid birth date");
                added = false;
            }

        } catch (ParseException e) {
            notifyWrongInsertion("Invalid birth date format");
            added = false;
        }

        if(added) {
            newPlayer = new Player(nickname, epoch);

            for(Player p : players)
                if(p.getBirthday() > newPlayer.getBirthday())
                    players.add(players.indexOf(p), newPlayer);
        }

        return added;
    }

    /** This method is used by game() to assign a godCard to a player
     * @param chosenGodCard the name of the GodCard the current player has chosen
     * It gives an error whether the player choose a different name from the ones printed (available)*/

    public boolean assignCard (String chosenGodCard) {
        boolean existing = false;
        for (String s: chosenCards) {
            if (chosenGodCard.equals(s)){
                notifyWrongInsertion("ERROR: GodCard name already used ");
                return false;
            }
        }
        for (String s: getGodNames())
            if (s.equals(chosenGodCard)) {
                existing = true;
                break;
            }

        if (!existing) {
            notifyWrongInsertion("ERROR: The name entered is not an existing godCard, choose from the available ones ");
            return false;
        }
        chosenCards.add(chosenGodCard);
        currPlayer.setGodCard(cardsParser.createCard(currPlayer, chosenGodCard));
        currPlayer.getGodCard().setGameMap(gameMap);
        return true;
    }

    /** This method is used by game() to create 2 builders for current player and assign the chosen color to them
     * @param chosenColor the name of the chosen color
     * It gives an error whether the player choose a different name from the ones printed */

    public boolean assignColor (String chosenColor) {

        boolean existing = false;
        for (String s: chosenColors) {
            if (chosenColor.equals(s)){
                notifyWrongInsertion("ERROR: Color already used, choose from the available ones ");
                return false;
            }
        }

        for (Builder.BuilderColor color: Builder.BuilderColor.values()) {
            if (color.toString().equals(chosenColor)) {
                existing = true;
                break;
            }
        }

        if (!existing) {
            notifyWrongInsertion("ERROR: The name entered is not an existing color, choose from the available ones ");
            return false;
        }

        chosenColors.add(chosenColor);
        currPlayer.setBuilders(new Builder(currPlayer, Builder.BuilderColor.valueOf(chosenColor)),
                new Builder(currPlayer, Builder.BuilderColor.valueOf(chosenColor)));
        return true;
    }

    public boolean setCurrPlayerBuilders(int builder1I, int builder1J, int builder2I, int builder2J) {
        boolean set = true;

        Cell cell1 = gameMap.getCell(builder1I, builder1J);
        Cell cell2 = gameMap.getCell(builder2I, builder2J);

        return cell1.isOccupied() && cell2.isOccupied() &&
                cell1.setOccupant(currPlayer.getBuilders().get(0)) && cell2.setOccupant(currPlayer.getBuilders().get(1));
    }

    /**
     * Called after every MOVE step to verify if the currPlayer won
     * @return true if the player wins, false if the winCondition didn't occur
     */
    public boolean hasWon () {
        return currPlayer.getGodCard().winCondition();
    }

    public String getCurrStep (Player currPlayer) {
        return currPlayer.getGodCard().getCurrState().toUpperCase();
    }

    /**
     * This method is called to find the possible destinations for both the builders of the currPlayer
     * @param builderIndex index of the Builder x of the currentPlayer
     * @return the possible destination cells for a MOVE
     */
    public ArrayList<Cell> possibleDstCells (int builderIndex, boolean buildDome) {
        ArrayList<Cell> possibleDstBuilder = new ArrayList<>();
        Builder builder = currPlayer.getBuilders().get(builderIndex);
        Cell src = builder.getCell();
        int x, y;
        int i_src = src.getI();
        int j_src = src.getJ();
        for (x = 0; x < IslandBoard.dimension; x++)
            for (y = 0; y < IslandBoard.dimension; y++){

                switch (currStep) {
                    case "MOVE":
                        if (IslandBoard.distanceOne(i_src, j_src, x, y) && currPlayer.getGodCard().askMove(i_src, j_src, x, y))
                            possibleDstBuilder.add(gameMap.getCell(x, y));

                    case "BUILD":
                        if ((x == i_src && y == j_src || IslandBoard.distanceOne(i_src, j_src, x, y)) &&
                                currPlayer.getGodCard().askBuild(i_src, j_src, x, y, buildDome))
                            possibleDstBuilder.add(gameMap.getCell(x, y));
                        //case BOTH exception
                }
            }
        return possibleDstBuilder;
    }

    /**
     * This method is called after every move, to control whether the currPlayer has lost (he can't move anywhere)
     */
    public boolean hasLostDuringMove () {
        if (possibleDstBuilder1 == null || possibleDstBuilder2 == null)
            throw new IllegalArgumentException("Possible destinations arrays can't be null ");
        if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty()) {

            notifyLoss(currPlayer.getNickname());
            deletePlayer(currPlayer);
            return true;
        }
        return false;
    }

    /**
     * This method is called after every build, to control whether the currPlayer has lost (he can't build anywhere)
     */
    public boolean hasLostDuringBuild () {
        if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty() &&
                possibleDstBuilder1forDome.isEmpty() && possibleDstBuilder2forDome.isEmpty()) {

            notifyLoss(currPlayer.getNickname());
            deletePlayer(currPlayer);
            return true;
        }
        return false;
    }

    public void effectiveBuild (Cell src, Cell dst, boolean buildDome) {
        currPlayer.getGodCard().build(src.getI(), src.getJ(), dst.getI(),dst.getJ(), buildDome);

        //build method increase the currStep of the player
        currStep = getCurrStep(currPlayer);
        if (!currStep.equals("END"))
            findPossibleDestinations();
        else
            setNextPlayer();
    }

    public void effectiveMove (Cell src, Cell dst) {
        currPlayer.getGodCard().move(src.getI(), src.getJ(), dst.getI(),dst.getJ());

        //move method increases the currStep of the player
        currStep = getCurrStep(currPlayer);
        if (!currStep.equals("END"))
            findPossibleDestinations();
        else
            setNextPlayer();
    }

    public void findPossibleDestinations () {
        currStep = getCurrStep(currPlayer);
        switch (currStep) {
            case "MOVE":
                //View has to obtain the list of the possible moves for both builders
                possibleDstBuilder1 = possibleDstCells(0, false);
                possibleDstBuilder2 = possibleDstCells(1, false);
                if (!hasLostDuringMove())
                    notifyPossibleMoves(possibleDstBuilder1, possibleDstBuilder2);
                break;

            case "BUILD":
                //View has to obtain the list of the possible build destinations for both builders and for the possible build of a dome
                possibleDstBuilder1 = possibleDstCells(0, false);
                possibleDstBuilder2 = possibleDstCells(0,true);
                possibleDstBuilder1forDome = possibleDstCells(0,true);
                possibleDstBuilder2forDome = possibleDstCells(1,true);
                if (!hasLostDuringBuild())
                    notifyPossibleBuilds(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome);
                break;
                //case END exception
        }
    }

    //Step in currPlayer.GodCard is BOTH, then I set the step in function of what user decides to do
    public void setStepChoice (String step) {
        if (step.equals("MOVE") || step.equals("BUILD"))
            this.currStep = step;
        else
            notifyWrongInsertion("ERROR: The step entered is not a valid value ");
    }

    protected IslandBoard getGameMap () {
        return gameMap;
    }

}