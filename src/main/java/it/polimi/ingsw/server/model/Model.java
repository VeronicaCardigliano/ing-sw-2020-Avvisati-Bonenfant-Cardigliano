package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.parser.GodCardParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author veronica
 * Model class that specifies the single match, containing the list of players, the gameMap,
 * the Set of chosen Cards and Colors and the jsonPath
 */

public class Model extends ModelObservableWithSelect {
    private final static String jsonPath = "src/main/java/it/polimi/ingsw/server/parser/cards.json";

    private static final int maxNumberOfPlayers = 3;
    private static final int minNumberOfPlayers = 2;
    private final ArrayList<Player> players = new ArrayList<>();
    private int numPlayers;
    private final IslandBoard gameMap;
    private final CyclingIterator<Player> turnManager = new CyclingIterator<>(players);

    //in this class currState refers to the match state, instead currStep refers to the single movement during GAME state
    public enum State { SETUP_NUMOFPLAYERS, SETUP_PLAYERS, SETUP_COLOR, SETUP_CARDS, SETUP_BUILDERS, GAME, ENDGAME }
    private State currState;
    private String currStep;
    private final GodCardParser cardsParser;

    private String startPlayerNickname;
    private String challenger;

    private Player currPlayer;
    private Builder chosenBuilder;
    Set<String> matchGodCards;
    Set<String> chosenCards = new HashSet<>();
    Set<String> chosenColors = new HashSet<>();

    /**
     * PossibleDst Arrays contains the list of Cells in which the player can move or build
     */
    protected Set<Coordinates> possibleDstBuilder1;
    protected Set<Coordinates> possibleDstBuilder2;
    protected Set<Coordinates> possibleDstBuilder1forDome;
    protected Set<Coordinates> possibleDstBuilder2forDome;


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
                currState = State.SETUP_COLOR;
                break;
            case SETUP_COLOR:
                currState = State.SETUP_BUILDERS;
                break;
            case SETUP_BUILDERS:
                currState = State.GAME;
                break;
            case GAME:
                currState = State.ENDGAME;
                break;
        }
        notifyState(currState);
    }


    public State getCurrState () {
        return this.currState;
    }

    public String getCurrPlayer () {
        return currPlayer.getNickname();
    }

    public String getCurrStep () {return currStep;}

    public void startTurn () {
        currPlayer.startTurn();
        currStep = currPlayer.getGodCard().getCurrState().toUpperCase();
    }

    public String getStartPlayerNickname() {return startPlayerNickname;}

    public void setStartPlayerNickname(String startPlayerNickname) {this.startPlayerNickname = startPlayerNickname;}

    public String getChallenger() {return challenger;}

    public void setChallenger(String challenger) {this.challenger = challenger;}

    public Set<String> getGodNames() {
        return this.cardsParser.getGodDescriptions().keySet();
    }

    public Map<String, String> getGodDescriptions() {
        return this.cardsParser.getGodDescriptions();
    }

    public Map<String, String> getMatchGodCardsDescriptions() {
        Map<String, String> result = new HashMap<>();

        Map<String, String> godDescriptions = getGodDescriptions();

        for(String godName : matchGodCards) {
            result.put(godName, godDescriptions.get(godName));
        }

        return result;
    }

    public Set<String> getChosenCards () {
        return this.chosenCards;
    }

    public Set<String> getChosenColors () {
        return this.chosenColors;
    }

    public void setNextPlayer () {
        if(players.isEmpty())
            throw new RuntimeException("there are no players in this lobby");
        currPlayer = turnManager.next();
        notifyPlayerTurn(currPlayer.getNickname());
    }

    /*
    public Set<String> getNicknames() {
        return this.players.stream().map(Player::getNickname).collect(Collectors.toSet());
    }
    */

    /**
     * @return returns a copy of the list of players so that external methods can't modify the ArrayList
     */
    public ArrayList<Player> getPlayers(){
        return new ArrayList<>(players);
    }


    public Set<String> getPlayersNickname() {
        return players.stream().map(Player::getNickname).collect(Collectors.toSet());
    }

    public ArrayList<String> getCurrStateList(){
        return currPlayer.getGodCard().getCurrStateList();
    }

    public boolean currPlayerNullGodCard() {
        return currPlayer.getGodCard() == null;
    }

    /**
     * deletes a player if he loses the match (if the number of players is three, the match will go on)
     * @param playerName who has lost the match
     * @exception IllegalArgumentException is thrown if the player isn't in the list of players of the match
     */
    public void deletePlayer (String playerName) throws IllegalArgumentException {

        boolean found = false;

        for (Player p : players)

            if (p.getNickname().equals(playerName)){

                for (Builder x : p.getBuilders()) {
                    x.getCell().removeOccupant();
                }

                found = players.remove(p);
            }

        if (!found)
            throw new IllegalArgumentException("Player not found");
    }

    public int getNumberOfPlayers(){
        return numPlayers;
    }

    public boolean setNumberOfPlayers(int num){

        for (int i = minNumberOfPlayers; i <= maxNumberOfPlayers; i++) {
            if (num == i) {
                numPlayers = num;
                return true;
            }
        }

        notifyWrongNumber(); //notifies the first view in viewManager
        return false;
    }

    /**
     * This method ensures that the players are added in order of birthday, from the youngest to the oldest
     * @param nickname: unique identifier of a new player
     * @param birthday: String that represents a birthday (yyyy.mm.dd)
     */
    public boolean addPlayer (String nickname, String birthday) {
        boolean canAdd = true;

        if (nickname == null) {
            canAdd = false;
        }

        if(players.size() >= numPlayers) {
            canAdd = false;
        }

        for(Player p : players)
            if(p.getNickname().equals(nickname)) {
                canAdd = false;
                break;
            }

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        long epoch = 0;

        try {
            epoch = df.parse(birthday).getTime();

            if(epoch > System.currentTimeMillis()) {
                canAdd = false;
            }

        } catch (ParseException e) {
            canAdd = false;
        }

        if(canAdd) {
            players.add(new Player(nickname, epoch));
            players.sort((a, b) -> {
                long difference = b.getBirthday() - a.getBirthday();
                if(difference < 0) return -1;
                if(difference > 0) return 1;
                return 0;
            });
        }

        notifyViewSelection(nickname);
        notifyPlayerAdded(nickname, canAdd);

        return canAdd;
    }


    public boolean setMatchCards(Set<String> matchGodCards) {
        boolean set = true;

        if(getGodDescriptions().keySet().containsAll(matchGodCards) && matchGodCards.size() == numPlayers) {
            this.matchGodCards = matchGodCards;
        }
        else {
            set = false;
        }

        notifyViewSelection(getCurrPlayer());
        notifyMatchGodCards(currPlayer.getNickname(), Set.copyOf(matchGodCards), set);

        return set;
    }

    /** This method is used by game() to assign a godCard to a player
     * @param chosenGodCard the name of the GodCard the current player has chosen
     * It gives an error whether the player choose a different name from the ones printed (available)*/

    public boolean assignCard (String chosenGodCard) {
        boolean existing = false;
        boolean assigned = true;

        notifyViewSelection(currPlayer.getNickname());

        for (String s: chosenCards) {
            if (chosenGodCard.equals(s)){
                notifyWrongInsertion("ERROR: GodCard name already used ");
                assigned = false;
                break;
            }
        }
        for (String s: getGodNames())
            if (s.equals(chosenGodCard)) {
                existing = true;
                break;
            }

        if (!existing) {
            notifyWrongInsertion("ERROR: The name entered is not an existing godCard, choose from the available ones ");
            assigned = false;
        }

        if (assigned) {
            chosenCards.add(chosenGodCard);
            currPlayer.setGodCard(cardsParser.createCard(currPlayer, chosenGodCard));
            currPlayer.getGodCard().setGameMap(gameMap);
        }

        notifyGodChoice(currPlayer.getNickname(), chosenGodCard, assigned);

        return assigned;
    }

    public boolean setStartPlayer(String challenger, String startPlayer) {
        boolean result = true;
        Set<String> nicknames = getPlayersNickname();

        notifyViewSelection(challenger);

        if(nicknames.contains(startPlayer))
            while(!currPlayer.getNickname().equals(startPlayer))
                currPlayer = turnManager.next();
        else
            result = false;

        notifyStartPlayerSet(startPlayer, result);

        return result;
    }

    /** This method is used by game() to create 2 builders for current player and assign the chosen color to them
     * @param chosenColor the name of the chosen color
     * It gives an error whether the player choose a different name from the ones printed */

    public boolean assignColor (String chosenColor) {

        boolean existing = false;
        boolean assigned = false;

        notifyViewSelection(currPlayer.getNickname());

        for (String s: chosenColors) {
            if (chosenColor.equals(s)){
                notifyWrongInsertion("ERROR: Color already used, choose from the available ones ");
                assigned = true;
            }
        }

        for (Builder.BuilderColor color: Builder.BuilderColor.values()) {
            if (color.toString().equals(chosenColor)) {
                existing = true;
                break;
            }
        }

        if (!assigned && existing) {
            chosenColors.add(chosenColor);
            currPlayer.setBuilders(new Builder(currPlayer, Builder.BuilderColor.valueOf(chosenColor)),
                    new Builder(currPlayer, Builder.BuilderColor.valueOf(chosenColor)));
        }

        notifyColorAssigned(currPlayer.getNickname(),chosenColor, !assigned && existing);

        return !assigned && existing ;
    }

    public boolean setCurrPlayerBuilders(Coordinates builder1Coord, Coordinates builder2Coord) {
        boolean set = false;

        notifyViewSelection(currPlayer.getNickname());

        Cell cell1 = gameMap.getCell(builder1Coord);
        Cell cell2 = gameMap.getCell(builder2Coord);

        if (!cell1.isOccupied() && !cell2.isOccupied() &&
                cell1.setOccupant(currPlayer.getBuilders().get(0)) && cell2.setOccupant(currPlayer.getBuilders().get(1))) {
            set = true;

        }

        notifyBuildersPlacement(currPlayer.getNickname(), builder1Coord, builder2Coord, set);
        return set;
    }

    /**
     * Called after every step to verify if the currPlayer won or if a player won remaining the only one who hasn't lost
     * @return true if someone wins, false otherwise
     */
    public boolean endGame() {
        boolean end = false;
        if(players.size() == 1) {
            end = true;
            notifyEndGame(players.get(0).getNickname());
        } else {
            for(Player p : players)
                if(p.getGodCard().winCondition()) {
                    notifyEndGame(p.getNickname());
                    currState = State.ENDGAME;
                    end = true;
                    break;
                }
        }
        return end;
    }


    /**
     * This method is called to find the possible destinations for both the builders of the currPlayer
     * @param builderIndex index of the Builder x of the currentPlayer
     * @return the possible destination cells for a MOVE
     */
    public Set<Coordinates> possibleDstCells (int builderIndex, boolean buildDome) {
        Set<Coordinates> possibleDstBuilder = new HashSet<>();
        Builder builder = currPlayer.getBuilders().get(builderIndex);

        notifyViewSelection(currPlayer.getNickname());

        Coordinates src = builder.getCell();
        int x, y;
        int i_src = src.getI();
        int j_src = src.getJ();
        for (x = 0; x < IslandBoard.dimension; x++)
            for (y = 0; y < IslandBoard.dimension; y++){

                switch (currStep) {
                    case "MOVE":
                        if (IslandBoard.distanceOne(i_src, j_src, x, y) && currPlayer.getGodCard().askMove(i_src, j_src, x, y)) {
                            possibleDstBuilder.add(new Coordinates(gameMap.getCell(x, y)));
                        }
                        break;

                    case "BUILD":
                        if ((x == i_src && y == j_src || IslandBoard.distanceOne(i_src, j_src, x, y)) &&
                                currPlayer.getGodCard().askBuild(i_src, j_src, x, y, buildDome)) {
                            possibleDstBuilder.add(new Coordinates(gameMap.getCell(x, y)));
                        }
                        break;
                }
            }
        return possibleDstBuilder;

    }

    /**
     * This method is called after every move, to control whether the currPlayer has lost (he can't move anywhere)
     */
    public boolean hasNotLostDuringMove() {
        if (possibleDstBuilder1 == null || possibleDstBuilder2 == null)
            throw new IllegalArgumentException("Possible destinations arrays can't be null ");

        if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty()) return false;
        return true;
    }

    /**
     * This method is called after every build, to control whether the currPlayer has lost (he can't build anywhere)
     */
    public boolean hasNotLostDuringBuild() {

        if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty() &&
                possibleDstBuilder1forDome.isEmpty() && possibleDstBuilder2forDome.isEmpty()) return false;
        return true;
    }


    public boolean effectiveBuild (Coordinates src, Coordinates dst, boolean buildDome) {
        boolean result = false;
        boolean correctBuilder = true;
        notifyViewSelection(currPlayer.getNickname());

        //save the builder if this is the first game step of currPlayer
        if (currPlayer.getGodCard().getStepNumber() == 0)
            chosenBuilder = gameMap.getCell(src).getBuilder();
        else if (!Coordinates.equals(chosenBuilder.getCell(), src)) {
            notifyWrongInsertion("ERROR: you have to continue the turn with the same builder ");
            correctBuilder = false;
        }

        if (correctBuilder)
            result = currPlayer.build(src.getI(), src.getJ(), dst.getI(),dst.getJ(), buildDome);

        if(result)
            currStep = currPlayer.getGodCard().getCurrState().toUpperCase();
        notifyBuilderBuild(currPlayer.getNickname(), src, dst, buildDome, result);

        return result;
    }

    public boolean effectiveMove (Coordinates src, Coordinates dst) {

        boolean result = false;
        boolean correctBuilder = true;
        notifyViewSelection(currPlayer.getNickname());

        //save the builder if this is the first game step of currPlayer
        if (currPlayer.getGodCard().getStepNumber() == 0)
            chosenBuilder = gameMap.getCell(src).getBuilder();
        else if (!Coordinates.equals(chosenBuilder.getCell(), src)) {
            notifyWrongInsertion("ERROR: you have to continue the turn with the same player ");
            correctBuilder = false;
        }

        if (correctBuilder) {
            Cell dstCell = gameMap.getCell(dst);
            Builder possibleEnemyToPush = dstCell.isOccupied() ? dstCell.getBuilder() : null;

            result = currPlayer.move(src.getI(), src.getJ(), dst.getI(),dst.getJ());

            if(result && possibleEnemyToPush != null) {
                notifyBuilderPushed(possibleEnemyToPush.getPlayer().getNickname(), dst, possibleEnemyToPush.getCell());
            }

            if(result)
                currStep = currPlayer.getGodCard().getCurrState().toUpperCase();
        }

        notifyBuilderMovement(currPlayer.getNickname(),src, dst, result);


        return result;
    }

    public void findPossibleDestinations () {
        currStep = currPlayer.getGodCard().getCurrState().toUpperCase();
        //boolean result = false;

        notifyViewSelection(currPlayer.getNickname());

        switch (currStep) {
            case "MOVE":
                //View has to obtain the list of the possible moves for both builders
                possibleDstBuilder1 = possibleDstCells(0, false);
                possibleDstBuilder2 = possibleDstCells(1, false);
                if (hasNotLostDuringMove()) {
                    notifyPossibleMoves(possibleDstBuilder1, possibleDstBuilder2);
                }
                break;

            case "BUILD":
                //View has to obtain the list of the possible build destinations for both builders and for the possible build of a dome
                possibleDstBuilder1 = possibleDstCells(0, false);
                possibleDstBuilder2 = possibleDstCells(1,false);
                possibleDstBuilder1forDome = possibleDstCells(0,true);
                possibleDstBuilder2forDome = possibleDstCells(1,true);
                if (hasNotLostDuringBuild()) {
                    notifyPossibleBuilds(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome);
                }
                break;
        }
    }

    /**
     * This method is called when Step in currPlayer.GodCard is REQUIRED
     * @param step is the effective step the user decides to do
     * @return if the step has been effectively set
     */
    public boolean setStepChoice (String step) {
        boolean changed = false;
        notifyViewSelection(currPlayer.getNickname());

        if (step.equals("MOVE") || step.equals("BUILD")) {
            currPlayer.forceStep(step);
            changed = true;
        }

        else {
            notifyWrongInsertion("ERROR: The step entered is not a valid value ");
        }
        notifyChosenStep(currPlayer.getNickname(), step, changed);

        return changed;
    }

}