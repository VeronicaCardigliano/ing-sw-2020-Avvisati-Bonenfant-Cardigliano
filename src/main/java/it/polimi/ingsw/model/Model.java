package it.polimi.ingsw.model;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author veronica
 * Model class that specifies the single match, containing the list of players, the IslandBoard,
 * the current player and a turnManager which iterates the list of players
 */

public class Model {
    private final static String jsonPath = "src/main/java/it/polimi/ingsw/model/cards.json";

    private final ArrayList<Player> players = new ArrayList<>();
    private final IslandBoard gameMap;
    private Player currPlayer;
    private CyclingIterator<Player> turnManager = new CyclingIterator<Player>(players);
    public enum State { SETUP_PLAYERS, SETUP_CARDS, SETUP_BUILDERS, GAME, ENDGAME }
    //in this class currState refers to the match state, instead currStep refers to the single movement during GAME state
    private State currState;
    private String currStep;
    private JSONObject jsonObject;
    private final int numPlayers;

    /**
     * possibleDst contains the list of Cells in which the player can move or build
     */
    protected ArrayList<Cell> possibleDstBuilder1;
    protected ArrayList<Cell> possibleDstBuilder2;
    protected ArrayList<Cell> possibleDstBuilder1forDome;
    protected ArrayList<Cell> possibleDstBuilder2forDome;

    /**
     * The constructor initialises the GameMap and assigns it to the GodCard as a static attribute, common to each card.
     * It also sets the currState to SETUP_PLAYERS, which is the first one, and loads the json file
     */

    public Model (int numPlayers) {
        this.gameMap = new IslandBoard();
        this.currState = State.SETUP_PLAYERS;
        this.numPlayers = numPlayers;

        //loading the json file to have access to all card names

        String JsonString = null;
        try {
            //I need to save the file content in a string
            JsonString = new String(Files.readAllBytes(Paths.get(jsonPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (JsonString != null)
            this.jsonObject = new JSONObject(JsonString);
        else
            this.jsonObject = null;
    }

    public Set<String> getGodNames() {
        return this.jsonObject.keySet();
    }

    /**
     * This method ensures that the players are added in order of birthday, from the youngest to the oldest
     * @param nickname: unique identifier of a new player
     * @throws IllegalArgumentException if the nickname has already been added
     */

    public boolean addPlayer (String nickname, String birthday) throws IllegalArgumentException {
        boolean added = false;

        if (nickname == null)
            throw new IllegalArgumentException("Nickname can't be null");
        for (Player x: players) {

            if (nickname.equals(x.getNickname()))
                throw new IllegalArgumentException("This nickname has already been used");

        }
        if (players.size() < 3) {
            Player newPlayer = new Player(nickname, birthday);
            for (Player x: players) {

                if (x.getBirthday() > newPlayer.getBirthday()) {
                    Player tmp = players.set(players.indexOf(x), newPlayer);
                    players.add (tmp);
                }
            }
            players.add(newPlayer);
            added = true;

        }
        return added;
    }

    /**
     *
     * @param nickname Player with a default birthday
     * @return true if the player has been added
     */
    public boolean addPlayer(String nickname) {
        boolean added = false;

        if (nickname == null)
            throw new IllegalArgumentException("Nickname can't be null");
        for (Player x: players) {

            if (nickname.equals(x.getNickname()))
                throw new IllegalArgumentException("This nickname has already been used");

        }
        if (players.size() < 3) {
            players.add(new Player(nickname));
            added = true;
        }
        return added;
    }


    /**
     * @return returns a copy of the list of players so that external methods can't modify the ArrayList
     */

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>(this.players);
        return players;
    }

    /**
     * deletes a player if he loses the match (if the number of players is three, the match will go on)
     * @param player who has lost the match
     * @exception IllegalArgumentException is thrown if the player isn't in the list of players of the match
     */

    public void deletePlayer (Player player) {
        boolean found = players.remove (player);

        for (Builder x: player.getBuilders()) {
            x.getCell().removeOccupant();
        }

        if (!found)
            throw new IllegalArgumentException("Player not found");
    }

    public State getState() {
        return currState;
    }

    /**
     * @author veronica
     * the method run has to manage turns, match states and movement steps using iterators
     * (a simple one for SETUP states and a cycling one for GAME state)
     *
     */
    public void run() {
        Scanner input = new Scanner(System.in);
        Iterator<Player> playersIterator = players.iterator();
        while (currState != State.ENDGAME) {
            switch (currState) {
                case SETUP_PLAYERS:
                    while (players.isEmpty() || players.size() < numPlayers) {
                        System.out.println("Insert Player name: ");
                        String nickname = input.nextLine();
                        System.out.println("Insert Birthday date in the form: yyyy mm dd ");
                        String birthday = input.nextLine();
                        addPlayer(nickname, birthday);
                    }
                    currPlayer = players.get(0);
                    currState = State.SETUP_CARDS;
                    break;

                case SETUP_CARDS:
                    while (playersIterator.hasNext()) {
                        System.out.println(getGodNames().toString());
                        System.out.println("Select your GodCard from the available ones");
                        String godCardName = input.nextLine();
                        currPlayer.setGodCard(GodCard.createCard(currPlayer, jsonObject.getJSONObject(godCardName)));
                        currPlayer.getGodCard().setGameMap(gameMap);
                        currPlayer = playersIterator.next();
                    }
                    currState = State.SETUP_BUILDERS;
                    break;

                case SETUP_BUILDERS:
                    while (playersIterator.hasNext()) {
                        System.out.println("Select a color for your Builders");
                        Builder.BuilderColor builderColor = Builder.BuilderColor.valueOf(input.nextLine().toUpperCase());
                        currPlayer.setBuilders(new Builder(currPlayer, builderColor), new Builder(currPlayer, builderColor));
                        currPlayer = playersIterator.next();
                    }
                    currState = State.GAME;
                    break;

                case GAME:
                    while (!currPlayer.getGodCard().winCondition() && players.size() != 1 ) {
                        currStep = currPlayer.getGodCard().currState;

                        if (currStep.equals("MOVE")) {
                            /*
                             * I can find the possible destinations for both the builders of the currPlayer (done) or
                             * wait for a choice and then find the correct destinations
                             * //Builder chosenBuilder = Controller.getChosenBuilder ();
                             */
                            for (Builder b : currPlayer.getBuilders()) {
                                Cell src = b.getCell();
                                int x, y;
                                int i_src = src.getI();
                                int j_src = src.getJ();
                                for (x = 0; x < IslandBoard.dimension; x++)
                                    for (y = 0; y < IslandBoard.dimension; y++)
                                        if (x != i_src && y != j_src && IslandBoard.distanceOne(i_src, j_src, x, y) &&
                                                currPlayer.getGodCard().askMove(i_src, j_src, x, y)) {
                                            if (b.equals(currPlayer.getBuilders().get(0)))
                                                possibleDstBuilder1.add(gameMap.getCell(x, y));
                                            else
                                                possibleDstBuilder2.add(gameMap.getCell(x, y));
                                        }
                            }
                            if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty()) {
                                System.out.println("Player " + currPlayer.getNickname() + " lost the game");
                                deletePlayer(currPlayer);
                            }
                            /*Have to get the chosen destination from the controller (between the possible ones) to do the
                            effective move (if the player hasn't lost)
                            currPlayer.getGodCard().move(cell src, cell dst)*/

                        } else if (currStep.equals("BUILD")) {

                            boolean buildDome = false; //controller must communicate whether the player wants to build a dome or not

                            for (Builder b : currPlayer.getBuilders()) {
                                Cell src = b.getCell();
                                int x, y;
                                int i_src = src.getI();
                                int j_src = src.getJ();
                                for (x = 0; x < IslandBoard.dimension; x++)
                                    for (y = 0; y < IslandBoard.dimension; y++)
                                        if (x != i_src && y != j_src && IslandBoard.distanceOne(i_src, j_src, x, y) &&
                                                currPlayer.getGodCard().askBuild(i_src, j_src, x, y, buildDome)) {
                                            if (b.equals(currPlayer.getBuilders().get(0))) {
                                                if (buildDome)
                                                    possibleDstBuilder1forDome.add(gameMap.getCell(x, y));
                                                else
                                                    possibleDstBuilder1.add(gameMap.getCell(x, y));
                                            } else if (buildDome)
                                                possibleDstBuilder2forDome.add(gameMap.getCell(x, y));
                                            else
                                                possibleDstBuilder2.add(gameMap.getCell(x, y));
                                        }
                            }
                            if (possibleDstBuilder1.isEmpty() && possibleDstBuilder2.isEmpty() &&
                                    possibleDstBuilder1forDome.isEmpty() && possibleDstBuilder2forDome.isEmpty()) {

                                System.out.println("Player " + currPlayer.getNickname() + " lost the game");
                                deletePlayer(currPlayer);

                            }
                        }
                        // if BOTH (I can either move or build) I could ask in advance for a choice

                        currPlayer = turnManager.next();
                    }

                    currState = State.ENDGAME;
                    break;
            }
            if (currPlayer.getGodCard().winCondition())
                System.out.println("Player " + currPlayer.getNickname() + " won the game!!!");
            else
                System.out.println("Player " + players.get(0).getNickname() + " won the game!!!");

        }
    }

}