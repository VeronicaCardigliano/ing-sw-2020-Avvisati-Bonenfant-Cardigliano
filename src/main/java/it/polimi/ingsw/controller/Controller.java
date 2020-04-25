package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.view.BuilderListener;
import it.polimi.ingsw.view.Observer;
import it.polimi.ingsw.view.View;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Controller implements Observer, BuilderListener {

    private Model model;
    private View view;
    private int numPlayers;
    private Player currPlayer;

    public enum State { SETUP_PLAYERS, SETUP_CARDS, SETUP_BUILDERS, GAME, ENDGAME }
    private State currState;

    /**
     * possibleDst contains the list of Cells in which the player can move or build
     */

    protected ArrayList<Cell> possibleDstBuilder1;
    protected ArrayList<Cell> possibleDstBuilder2;
    protected ArrayList<Cell> possibleDstBuilder1forDome;
    protected ArrayList<Cell> possibleDstBuilder2forDome;

    public Controller (Model model, View view) {

        this.model = model;
        this.view = view;
        this.currState = State.SETUP_PLAYERS;

    }

    private void setupPlayers (Scanner input) {
        boolean correctNumPlayers = false;

        while (!correctNumPlayers) {
            System.out.println("Insert the number of players ");
            numPlayers = Integer.parseInt(input.nextLine());
            if (numPlayers == 2 || numPlayers == 3)
                correctNumPlayers = true;
            else
                System.out.println("ERROR: Number of players is wrong. Insert 2 or 3 ");
        }

        while (model.getPlayers().isEmpty() || model.getPlayers().size() < numPlayers) {
            System.out.println("Insert Player name: ");
            String nickname = input.nextLine();
            System.out.println("Insert Birthday date in the form \"yyyy.MM.dd\" ");
            String birthday = input.nextLine();
            model.addPlayer(nickname, birthday);
        }
        currPlayer = model.getPlayers().get(0);
    }

    private void setupCards (Scanner input) {
        ArrayList<Player> players = model.getPlayers();
        for (Player player : players) {
            boolean correctValue = false;
            while (!correctValue) {
                boolean alreadyUsed = false;
                //prints the GodNames and their description only of still available cards
                for (String s : model.getGodNames()) {
                    for (String x : model.getChosenCards()) {
                        if (s.equals(x)) {
                            alreadyUsed = true;
                            break;
                        }
                    }
                    if (!alreadyUsed) {
                        System.out.println(s);
                        System.out.println(model.getGodDescription(s));
                    }
                }
                System.out.println("Select your GodCard from the available ones");
                String godCardName = input.nextLine();
                correctValue = model.assignCard(player, godCardName);
            }
        }
    }

    private void setupBuilders (Scanner input) {
        ArrayList<Player> players = model.getPlayers();
        for (Player player : players) {
            boolean correctValue = false;
            while (!correctValue) {
                boolean alreadyUsed = false;
                System.out.println("Available builder colors: ");
                //prints the colors only if they're still available
                for (Builder.BuilderColor color : Builder.BuilderColor.values()) {
                    for (String alreadyChosen : model.getChosenColors()) {
                        if (alreadyChosen.equals(color.toString()))
                            alreadyUsed = true;
                    }
                    if (!alreadyUsed)
                        System.out.println(color.name().toUpperCase() + " ");
                }

                System.out.println("Select a color for your Builders ");
                String chosenColor = input.nextLine().toUpperCase();
                correctValue = model.assignColor(player, chosenColor);
            }
        }
    }

    private void gameMoves (Scanner input) {
        ArrayList<Player> players = model.getPlayers();
        CyclingIterator<Player> turnManager = new CyclingIterator<>(players);
        //I need to take Players Array everytime since it's a copy and doesn't refresh
        while (!model.hasWon(currPlayer) && model.getPlayers().size() > 1) {
            //Controller has to obtain the list of the possible moves for both builders and for the possible build of a dome
            switch (model.getCurrStep(currPlayer)) {
                case "MOVE":
                    possibleDstBuilder1 = model.getPossibleDstCellsMove(currPlayer, currPlayer.getBuilders().get(0));
                    possibleDstBuilder2 = model.getPossibleDstCellsMove(currPlayer, currPlayer.getBuilders().get(1));
                    //hasLost deletes the player in case he lost and notifies the view
                    model.hasLostAfterMove(currPlayer, possibleDstBuilder1, possibleDstBuilder2);
                    break;
                case "BUILD":
                    possibleDstBuilder1 = model.getPossibleDstCellsBuild(currPlayer, currPlayer.getBuilders().get(0), false);
                    possibleDstBuilder2 = model.getPossibleDstCellsBuild(currPlayer, currPlayer.getBuilders().get(1), false);
                    possibleDstBuilder1forDome = model.getPossibleDstCellsBuild(currPlayer, currPlayer.getBuilders().get(0), true);
                    possibleDstBuilder2forDome = model.getPossibleDstCellsBuild(currPlayer, currPlayer.getBuilders().get(1), true);
                    //hasLost deletes the player in case he lost and notifies the view
                    model.hasLostAfterBuild(currPlayer, possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome);
                    break;
                case "BOTH":
                    //wait for a choice from the view
                    break;
            }
            currPlayer = turnManager.next();
            //a special notify of view have to pass buildDome flag, src Cell and dst Cell
        }
    }


    /**
     * the method game has to manage turns, match states and movement steps
     * cycling once for each setup phases and using a cycling iterator for the game one
     *
     * @param source has to be System.in unless it's a test
     */

    private void game(InputStream source) {
        Scanner input = new Scanner(source);
        while (currState != State.ENDGAME) {
            switch (currState) {
                case SETUP_PLAYERS:
                    setupPlayers(input);
                    currState = State.SETUP_CARDS;
                    break;

                case SETUP_CARDS:
                    setupCards(input);
                    currState = State.SETUP_BUILDERS;

                case SETUP_BUILDERS:
                    setupBuilders(input);
                    currState = State.GAME;

                case GAME:
                    gameMoves(input);
                    currState = State.ENDGAME;
                    break;
            }
        }
        //notify to the view about the end of the game and the view'll se who won
        /*
        if (model.hasWon(currPlayer))
            System.out.println("Player " + currPlayer.getNickname() + " won the game!!!");
        else
            System.out.println("Player " + model.getPlayers().get(0).getNickname() + " won the game!!!");
        */
    }

    @Override
    public void update(Object message) {
        //changes to model
        game(System.in);
    }

    @Override
    public void onBuilderAction(Builder x, Cell src, Cell dst) {

    }
}
