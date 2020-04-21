package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.godCards.GodCard;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
    private final int numPlayers;
    private Player currPlayer;
    private CyclingIterator<Player> turnManager = new CyclingIterator<>(players);
    public enum State { SETUP_PLAYERS, SETUP_CARDS, SETUP_BUILDERS, GAME, ENDGAME }
    //in this class currState refers to the match state, instead currStep refers to the single movement during GAME state
    private State currState;
    private String currStep;
    private JSONObject jsonObject;

    private Controller controller;

    Set<String> chosenCards = new HashSet<>();
    Set<String> chosenColors = new HashSet<>();

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

    /**
     *
     * @param nickname Player with a default birthday
     * @return true if the player has been added
     */
    public boolean addPlayer(String nickname) {
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

        players.add(new Player(nickname));
        return true;
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

    //TODO: manage properly the interaction Model - Controller

    /*


     * This method is used by run() to assign a godCard to a player
     * @param currPlayer is the player who's currently choosing his GodCard
     * @param chosenGodCard the name of GodCard the current player has chosen
     * @throws IllegalArgumentException whether the player choose a different name from the ones printed


    private boolean assignCard (Player currPlayer, String chosenGodCard) {
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
        currPlayer.setGodCard(GodCard.createCard(currPlayer, jsonObject.getJSONObject(chosenGodCard)));
        currPlayer.getGodCard().setGameMap(gameMap);
        return true;
    }

    private boolean assignColor (Player currPlayer, Builder.BuilderColor chosenColor) {
        String chosenColorString = chosenColor.toString().toUpperCase();
        boolean existing = false;
        for (String s: chosenColors) {
            if (chosenColorString.equals(s)){
                System.out.println("ERROR: Color already used, choose from the available ones ");
                return false;
            }
        }

        for (Builder.BuilderColor color: Builder.BuilderColor.values()) {
            if (color.equals(chosenColor)) {
                existing = true;
                break;
            }
        }
        if (!existing) {
            System.out.println("ERROR: The name entered is not an existing color, choose from the available ones ");
            return false;
        }

        chosenColors.add(chosenColorString);
        currPlayer.setBuilders(new Builder(currPlayer, chosenColor), new Builder(currPlayer, chosenColor));
        return true;
    }



     *
     * the method run has to manage turns, match states and movement steps using iterators
     * (a simple one for SETUP states and a cycling one for GAME state)
     *
     * @param source has to be System.in unless it's a test

    public void run (InputStream source) {
        Scanner input = new Scanner(source);
        Iterator<Player> playersIterator = players.iterator();
        while (currState != State.ENDGAME) {
            switch (currState) {
                case SETUP_PLAYERS:
                    while (players.isEmpty() || players.size() < numPlayers) {
                    System.out.println("Insert Player name: ");
                    String nickname = input.nextLine();
                    System.out.println("Insert Birthday date in the form \"yyyy.MM.dd\" ");
                    String birthday = input.nextLine();
                    addPlayer(nickname, birthday);
                    }
                    currPlayer = players.get(0);
                    currState = State.SETUP_CARDS;
                    break;

                case SETUP_CARDS:
                    while (playersIterator.hasNext()) {
                        boolean correctValue = false;
                        while (!correctValue) {
                            boolean alreadyUsed = false;
                            //prints the GodNames and their description only of still available cards
                            for (String s: getGodNames()) {
                                for (String x: chosenCards) {
                                    if (s.equals(x)) {
                                        alreadyUsed = true;
                                        break;
                                    }
                                }
                                if (!alreadyUsed) {
                                    System.out.println(s);
                                    System.out.println(jsonObject.getJSONObject(s).getString("description"));
                                }
                            }
                            System.out.println("Select your GodCard from the available ones");
                            String godCardName = input.nextLine();
                            correctValue = assignCard (currPlayer, godCardName);
                        }
                        currPlayer = playersIterator.next();
                    }
                    currState = State.SETUP_BUILDERS;
                    break;

                case SETUP_BUILDERS:
                    while (playersIterator.hasNext()) {
                        boolean correctValue = false;
                        while (!correctValue)
                        {
                            boolean alreadyUsed = false;
                            System.out.println("Available builder colors: ");
                            //prints the colors only if they're still available
                            for (Builder.BuilderColor color: Builder.BuilderColor.values()) {
                                for (String alreadyChosen: chosenColors) {
                                    if (alreadyChosen.equals(color.toString()))
                                        alreadyUsed = true;
                                }
                                if (!alreadyUsed)
                                    System.out.println(color.name().toUpperCase() + " ");
                            }

                            System.out.println("Select a color for your Builders ");
                            Builder.BuilderColor chosenColor = Builder.BuilderColor.valueOf(input.nextLine().toUpperCase());
                            correctValue = assignColor (currPlayer,chosenColor);
                        }
                        currPlayer = playersIterator.next();
                    }
                    currState = State.GAME;
                    break;

                case GAME:
                    while (!currPlayer.getGodCard().winCondition() && players.size() > 1) {

                        currStep = currPlayer.getGodCard().getCurrState();

                        if (currStep.equals("MOVE")) {
                            /*
                             * Finds the possible destinations for both the builders of the currPlayer

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
                            Builder chosenBuilder = Controller.getChosenBuilder ();
                            currPlayer.getGodCard().move(cell src, cell dst)

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
//            if (currPlayer.getGodCard().winCondition())
 //               System.out.println("Player " + currPlayer.getNickname() + " won the game!!!");
            //          else
     //           System.out.println("Player " + players.get(0).getNickname() + " won the game!!!");

        }
    } */

}