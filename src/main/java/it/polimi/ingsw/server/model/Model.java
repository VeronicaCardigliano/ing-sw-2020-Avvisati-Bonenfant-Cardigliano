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
 * Model class that specifies the single match, containing the list of players, the gameMap,
 * the Set of chosen Cards and Colors and the jsonPath.
 * PossibleDst Arrays contains the list of Cells in which the player can move or build
 */

public class Model extends ModelObservableWithSelect {
    public final static String jsonPath = "/cards.json";
    private static final int maxNumberOfPlayers = 3;
    private static final int minNumberOfPlayers = 2;
    private static final int firstBuilderIndex = 0;
    private final ArrayList<Player> players = new ArrayList<>();
    private int numPlayers;
    private final IslandBoard gameMap;
    private final CyclingIterator<Player> turnManager = new CyclingIterator<>(players);
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

    protected Set<Coordinates> possibleDstBuilder1 = new HashSet<>();
    protected Set<Coordinates> possibleDstBuilder2 = new HashSet<>();
    protected Set<Coordinates> possibleDstBuilder1forDome = new HashSet<>();
    protected Set<Coordinates> possibleDstBuilder2forDome = new HashSet<>();


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
        chosenBuilder = null;
        currStep = currPlayer.getGodCard().getCurrState().toUpperCase();
    }

    public void loadConstrain(){
        gameMap.loadConstraint();
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
        if (currState.equals(State.SETUP_CARDS) && numPlayers == chosenCards.size()) {
            //currPlayer is the challenger since he has to choose the startPlayer
            while (!currPlayer.equals(players.get(0)))
                currPlayer = turnManager.next();
        }
        else
            currPlayer = turnManager.next();

        notifyPlayerTurn(currPlayer.getNickname());
    }


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
     * Deletes a player if he loses the match (if the number of players is three, the match will go on)
     * @param playerName Who has lost the match or cannot move anymore
     * @exception IllegalArgumentException is thrown if the player isn't in the list of players of the match
     */
    public void deletePlayer (String playerName) throws IllegalArgumentException {

        boolean found = false;
        for (Player p : players)
            if (p.getNickname().equals(playerName)){
                for (Builder x : p.getBuilders()) {
                    x.getCell().removeOccupant();
                }
                found = true;
                break;
            }

        if(found)
            players.removeIf(p -> p.getNickname().equals(playerName));
        else
            throw new IllegalArgumentException("Player not found");
    }

    public int getNumberOfPlayers(){
        return numPlayers;
    }

    /**
     * @param num Number of players that will play in the game
     */
    public boolean setNumberOfPlayers(int num){
        if (minNumberOfPlayers <= num && num <= maxNumberOfPlayers){
            this.numPlayers = num;
            return true;
        }

        notifyWrongNumber();
        return false;
    }

    /**
     * This method ensures that the players are added in order of birthday, from the youngest to the oldest
     * @param nickname Unique identifier of a new player
     * @param birthday String that represents a birthday (yyyy.mm.dd)
     */
    public boolean addPlayer (String nickname, String birthday) {
        boolean canAdd = true;
        boolean correctDate = checkDate(birthday);

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        long epoch = 0;

        try {
            epoch = df.parse(birthday).getTime();
        }
        catch (ParseException e) {
            notifyWrongInsertion("ERROR: invalid date format");
            canAdd = false;
        }

        if (nickname == null || (canAdd && !correctDate)) {
            if (!correctDate)
                notifyWrongInsertion("ERROR: invalid date insertion");
            canAdd = false;
        }

        if(players.size() >= numPlayers) {
            notifyWrongInsertion("ERROR: Max number of players reached");
            canAdd = false;
        }

        for(Player p : players)
            if(p.getNickname().equals(nickname)) {
                notifyWrongInsertion("ERROR: this nickname already exists");
                canAdd = false;
                break;
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

        notifyPlayerAdded(nickname, canAdd);

        return canAdd;
    }

    /**
     * Check if a given date is valid. Leap year control is enabled
     * @param date Date as string in format YYYY.MM.DD
     */
    protected static boolean checkDate(String date) {
        boolean dateOk = false;
        List<String> components = Arrays.asList(date.split("\\."));

        int inputYear, inputMonth, inputDay;
        int thisYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int thisMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int thisDay = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));


        if (date.matches("\\d{4}\\.\\d{2}\\.\\d{2}")){
            inputYear = Integer.parseInt(components.get(0));
            inputMonth = Integer.parseInt(components.get(1));
            inputDay = Integer.parseInt(components.get(2));

            if (inputYear > thisYear)
                return false;
            else if (inputYear == thisYear){
                if (inputMonth > thisMonth)
                    return false;
                else if (inputMonth == thisMonth){
                    if (inputDay > thisDay)
                        return false;
                }
            }

            boolean isLeap = false;
            if (inputYear % 100 == 0 && inputYear % 4 == 0 && inputYear % 400 == 0)
                isLeap = true;

            if(inputYear > 0 && inputMonth >= 1 && inputMonth <= 12) {
                switch (inputMonth) {
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        dateOk = inputDay >= 1 && inputDay <= 30;
                        break;
                    case 2:
                        if (isLeap)
                            dateOk = inputDay >= 1 && inputDay <= 29;
                        else
                            dateOk = inputDay >= 1 && inputDay <= 28;
                        break;
                    default:
                        dateOk = inputDay >= 1 && inputDay <= 31;
                        break;
                }
            }
        }
        return dateOk;
    }


    /**
     * @param matchGodCards Set of god cards the challenger has chose
     */
    public boolean setMatchCards(Set<String> matchGodCards) {
        boolean set = true;

        if(getGodDescriptions().keySet().containsAll(matchGodCards) && matchGodCards.size() == numPlayers) {
            this.matchGodCards = matchGodCards;
        }
        else {
            set = false;
        }

        notifyViewSelection(getCurrPlayer());
        notifyMatchGodCards(Set.copyOf(matchGodCards), set);

        return set;
    }


    /** This method used to assign a god card to a player
     * @param chosenGodCard Name of the GodCard the current player has chosen
     * @return False if the god card doesn't exist or isn't in the list of the available ones
     */
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


    /**
     * Sets start player
     * @param startPlayer Nickname of the player chosen to start the game
     * @return True if the start player is assigned
     */
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


    /** Assign a color to the current player and to all his builders. Gives an error whether the player choose a
     * different color from the ones available
     * @param chosenColor Name of the chosen color
     */
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


    /**
     * Place builders of the current player on the game map
     * @param builder1Coord Coordinates where place first builder
     * @param builder2Coord Coordinates where place second builder
     * @return True if builders are placed
     */
    public boolean setCurrPlayerBuilders(Coordinates builder1Coord, Coordinates builder2Coord) {
        boolean set = false;

        notifyViewSelection(currPlayer.getNickname());

        Cell cell1 = gameMap.getCell(builder1Coord);
        Cell cell2 = gameMap.getCell(builder2Coord);

        if (cell1 != cell2 && !cell1.isOccupied() && !cell2.isOccupied() &&
                cell1.setOccupant(currPlayer.getBuilders().get(0)) && cell2.setOccupant(currPlayer.getBuilders().get(1))) {
            set = true;

        }

        notifyBuildersPlacement(currPlayer.getNickname(), builder1Coord, builder2Coord, set);
        return set;
    }


    /**
     * Called after every step to verify if the currPlayer won or if a player won remaining the only one who hasn't lost
     * @return True if someone wins
     */
    public boolean endGame() {
        boolean end = false;
        if(players.size() == 1) {
            end = true;
            notifyEndGame(players.get(0).getNickname());
        }
        else {
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
     * Find the possible destinations for both the builders of the current player
     * @param builderIndex index of the Builder x of the currentPlayer
     * @return the possible destination cells for a MOVE
     */
    public Set<Coordinates> possibleDstCells (int builderIndex, boolean buildDome) {
        Builder builder = currPlayer.getBuilders().get(builderIndex);
        notifyViewSelection(currPlayer.getNickname());
        return currPlayer.getGodCard().findBuilderPossibleDest(builder, buildDome);
    }

    /**
     * This method is called after every move to control if the current player has lost
     */
    public boolean hasNotLostDuringMove() {
        if (chosenBuilder == null)
            return !possibleDstBuilder1.isEmpty() || !possibleDstBuilder2.isEmpty();
        else {
            if (chosenBuilder.equals(currPlayer.getBuilders().get(firstBuilderIndex)))
                return !possibleDstBuilder1.isEmpty();
            else
                return !possibleDstBuilder2.isEmpty();
        }
    }

    /**
     * This method is called after every build to control if the current player has lost
     */
    public boolean hasNotLostDuringBuild() {
        if (chosenBuilder == null)
            return !possibleDstBuilder1.isEmpty() || !possibleDstBuilder1forDome.isEmpty() ||
                    !possibleDstBuilder2.isEmpty() || !possibleDstBuilder2forDome.isEmpty();
        else {
            if (chosenBuilder.equals(currPlayer.getBuilders().get(firstBuilderIndex)))
                return !possibleDstBuilder1.isEmpty() || !possibleDstBuilder1forDome.isEmpty();
            else
                return !possibleDstBuilder2.isEmpty() || !possibleDstBuilder2forDome.isEmpty();
        }
    }


    /**
     * Main function called to perform a build. This function not only tries to build but it also notifies the view
     * depending on the result
     * @param src Source should contain a builder of the current player
     * @return True if move is done
     */
    public boolean effectiveBuild (Coordinates src, Coordinates dst, boolean buildDome) {
        boolean result = false;
        boolean correctBuilder = true;
        notifyViewSelection(currPlayer.getNickname());
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


    /**
     * Main function called to perform a move
     * @param src Source should contain a builder of the current player. This function not only tries to move but
     * it also notifies the view depending on the result
     * @return True if move is done
     */
    public boolean effectiveMove (Coordinates src, Coordinates dst) {
        boolean result = false;
        boolean correctBuilder = true;
        notifyViewSelection(currPlayer.getNickname());
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


    /**
     * For each builder of the current player gets the list of all possible move and build
     */
    public void findPossibleDestinations () {
        currStep = currPlayer.getGodCard().getCurrState().toUpperCase();

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
        else
            notifyWrongInsertion("ERROR: The step entered is not a valid value ");
        notifyChosenStep(currPlayer.getNickname(), step, changed);
        return changed;
    }

}