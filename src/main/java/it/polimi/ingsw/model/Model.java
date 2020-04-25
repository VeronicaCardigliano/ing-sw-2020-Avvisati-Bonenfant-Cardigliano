package it.polimi.ingsw.model;

import it.polimi.ingsw.parser.GodCardParser;

import java.util.*;

/**
 * @author veronica
 * Model class that specifies the single match, containing the list of players, the gameMap,
 * the Set of chosen Cards and Colors and the jsonPath
 */

public class Model {
    private final static String jsonPath = "src/main/java/it/polimi/ingsw/parser/cards.json";

    private final ArrayList<Player> players = new ArrayList<>();
    private final IslandBoard gameMap;
    //in this class currState refers to the match state, instead currStep refers to the single movement during GAME state
    //private String currStep;
    private GodCardParser cardsParser;

    Set<String> chosenCards = new HashSet<>();
    Set<String> chosenColors = new HashSet<>();

    /**
     * The constructor initialises the GameMap and assigns it to the GodCard as a static attribute, common to each card.
     * It also sets the currState to SETUP_PLAYERS, which is the first one, and loads the json file
     */

    public Model () {

        this.gameMap = new IslandBoard();
        this.cardsParser = new GodCardParser(jsonPath);

    }

    public Set<String> getGodNames() {
        return this.cardsParser.getGodNames();
    }

    public String getGodDescription(String godName) { return this.cardsParser.getDescription(godName); }

    public Set<String> getChosenCards () {
        return this.chosenCards;
    }

    public Set<String> getChosenColors () {
        return this.chosenColors;
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

    /**
     * This method ensures that the players are added in order of birthday, from the youngest to the oldest
     * @param nickname: unique identifier of a new player
     * @throws IllegalArgumentException if the nickname has already been added
     */

    public boolean addPlayer (String nickname, String birthday) {
        boolean younger = false;
        Player tmp = null;

        if (nickname == null) {
            System.out.println("ERROR: Nickname can't be null ");
            return false;
        }
        for (Player x: players) {
            if (nickname.equals(x.getNickname())) {
                System.out.println("ERROR: This nickname has already been used");
                return false;
            }
        }
        Player newPlayer = new Player(nickname, birthday);
        for (Player x: players)
            //birthday is the distance since the epoch 1970-01-01 00:00:00.0 so the shorter it is, the older is the player
            if (x.getBirthday() < newPlayer.getBirthday()) {
                tmp = players.set(players.indexOf(x), newPlayer);
                younger = true;
            }

        if (younger)
            players.add(tmp);
        else {
            //newPlayer take the place of x and is returned in tmp
            players.add(newPlayer);
        }
        return true;
    }

    /** This method is used by game() to assign a godCard to a player
     * @param currPlayer is the player who's currently choosing his GodCard
     * @param chosenGodCard the name of the GodCard the current player has chosen
     * It gives an error whether the player choose a different name from the ones printed (available)*/

    public boolean assignCard (Player currPlayer, String chosenGodCard) {
        boolean existing = false;
        for (String s: chosenCards) {
            if (chosenGodCard.equals(s)){
                System.out.println("ERROR: GodCard name already used ");
                return false;
            }
        }
        for (String s: getGodNames())
            if (s.equals(chosenGodCard)) {
                existing = true;
                break;
            }

        if (!existing) {
            System.out.println("ERROR: The name entered is not an existing godCard, choose from the available ones ");
            return false;
        }
        chosenCards.add(chosenGodCard);
        currPlayer.setGodCard(cardsParser.createCard(currPlayer, chosenGodCard));
        currPlayer.getGodCard().setGameMap(gameMap);
        return true;
    }

    /** This method is used by game() to assign a color to a player
     * @param currPlayer is the player who's currently choosing his Builders' color
     * @param chosenColor the name of the chosen color
     * It gives an error whether the player choose a different name from the ones printed */

    public boolean assignColor (Player currPlayer, String chosenColor) {

        boolean existing = false;
        for (String s: chosenColors) {
            if (chosenColor.equals(s)){
                System.out.println("ERROR: Color already used, choose from the available ones ");
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
            System.out.println("ERROR: The name entered is not an existing color, choose from the available ones ");
            return false;
        }

        chosenColors.add(chosenColor);
        currPlayer.setBuilders(new Builder(currPlayer, Builder.BuilderColor.valueOf(chosenColor)),
                new Builder(currPlayer, Builder.BuilderColor.valueOf(chosenColor)));
        return true;
    }

    /**
     * Called after every MOVE step to verify if the currPlayer won
     * @param currPlayer the player who just moved
     * @return true if the player wins, false if the winCondition didn't occur
     */
    public boolean hasWon (Player currPlayer) {
        return currPlayer.getGodCard().winCondition();
    }

    public String getCurrStep (Player currPlayer) {
        return currPlayer.getGodCard().getCurrState();
    }

    /**
     * This method is called to find the possible destinations for both the builders of the currPlayer
     * @param currPlayer the player that is going to move
     * @param builder his pawn
     * @return the possible destination cells for a MOVE
     */
    public ArrayList<Cell> getPossibleDstCellsMove (Player currPlayer, Builder builder) {
        ArrayList<Cell> possibleDstBuilder = new ArrayList<>();
        Cell src = builder.getCell();
        int x, y;
        int i_src = src.getI();
        int j_src = src.getJ();
        for (x = 0; x < IslandBoard.dimension; x++)
            for (y = 0; y < IslandBoard.dimension; y++)
                if (IslandBoard.distanceOne(i_src, j_src, x, y) && currPlayer.getGodCard().askMove(i_src, j_src, x, y))
                    possibleDstBuilder.add(gameMap.getCell(x, y));
        return possibleDstBuilder;
    }

    /**
     * This method is called to find the possible destinations for both the builders of the currPlayer
     * @param currPlayer the player that is going to build
     * @param builder his pawn
     * @return the possible destination cells for a BUILD
     */
    public ArrayList<Cell> getPossibleDstCellsBuild (Player currPlayer, Builder builder, boolean buildDome) {
        ArrayList<Cell> possibleDstBuilder = new ArrayList<>();
        Cell src = builder.getCell();
        int x, y;
        int i_src = src.getI();
        int j_src = src.getJ();
        for (x = 0; x < IslandBoard.dimension; x++)
            for (y = 0; y < IslandBoard.dimension; y++)
                if ((x == i_src && y == j_src || IslandBoard.distanceOne(i_src, j_src, x, y)) &&
                        currPlayer.getGodCard().askBuild(i_src, j_src, x, y, buildDome))
                    possibleDstBuilder.add(gameMap.getCell(x, y));
        return possibleDstBuilder;
    }

    /**
     * This method is called after every move, to control whether the currPlayer has lost (he can't move anywhere)
     * @param currPlayer the player that's going to move
     * @param possibleDstBuilder1 possible dst cells for builder1
     * @param possibleDstBuilder2 possible dst cells for builder2
     */
    public void hasLostAfterMove (Player currPlayer, ArrayList<Cell> possibleDstBuilder1, ArrayList<Cell> possibleDstBuilder2) {
        if (possibleDstBuilder1 == null || possibleDstBuilder2 == null)
            throw new IllegalArgumentException("Possible destinations arrays can't be null ");
        if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty()) {
            // notifies the view (?)
            //System.out.println("Player " + currPlayer.getNickname() + " lost the game");
            deletePlayer(currPlayer);
        }
    }

    /**
     * This method is called after every build, to control whether the currPlayer has lost (he can't build anywhere)
     * @param currPlayer the player that's going to build
     * @param possibleDstBuilder1 possible dst cells for builder1
     * @param possibleDstBuilder2 possible dst cells for builder2
     */
    public void hasLostAfterBuild (Player currPlayer, ArrayList<Cell> possibleDstBuilder1, ArrayList<Cell> possibleDstBuilder2,
                                   ArrayList<Cell> possibleDstDome1, ArrayList<Cell> possibleDstDome2) {
        if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty() &&
                possibleDstDome1.isEmpty() && possibleDstDome2.isEmpty()) {
            // notifies the view (?)
            //System.out.println("Player " + currPlayer.getNickname() + " lost the game");
            deletePlayer(currPlayer);
        }
    }

}