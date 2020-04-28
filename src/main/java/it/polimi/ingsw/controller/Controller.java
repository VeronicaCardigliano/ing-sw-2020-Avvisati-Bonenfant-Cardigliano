package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.view.View;

//TODO: make sure of not passing editable objects

public class Controller implements BuilderBuildObserver, BuilderMoveObserver, NewPlayerObserver, NumberOfPlayersObserver,
            GodCardChoiceObserver, ColorChoiceObserver, StepChoiceObserver {

    private Model model;
    private View view;
    private int numPlayers;
    private Player currPlayer;

    private Model.State currState;

    public Controller (Model model, View view) {

        this.model = model;
        this.view = view;

    }

    /**
     * SetupPlayers manages the adding of numPlayers player, when it quits the while, all players have been added
     * and it sets the current Player to the younger one who'll chose for first his GodCard
     */
    private void setupPlayers () {
        while (model.getPlayers().isEmpty() || model.getPlayers().size() < numPlayers) {

            view.askForNewPlayer();
        }
        //when I get out of the while, it means I've added all the players

        model.setNextPlayer();
        currPlayer = model.getCurrPlayer();
    }

    private void setupCards () {
        int i = 0;
        while (i < numPlayers) {
            //prints the GodNames and their description only of still available cards
            view.chooseGodCard(model.getGodNames(), model.getChosenCards());

            if (!currPlayer.getGodCard().equals(null)) {
                i++;
                model.setNextPlayer();
                currPlayer = model.getCurrPlayer();
            }
        }
        //returns to the first player
        model.setNextPlayer();
        currPlayer = model.getCurrPlayer();
    }

    private void setupBuilders () {
        int i = 0;
        while (i < numPlayers) {

            view.chooseBuilderColor(model.getChosenColors());

            if(!currPlayer.getBuilders().equals(null)) {
                i++;
                model.setNextPlayer();
                currPlayer = model.getCurrPlayer();
            }
        }
        model.setNextPlayer();
        currPlayer = model.getCurrPlayer();
    }

    private void gameMoves () {
        boolean win = false;

        while (!win && model.getPlayers().size() > 1) {
            //the Step is updated after every effective move or build from the methods themselves (move and build)
            switch (model.getCurrStep(currPlayer)) {
                case "MOVE":
                    //I send through a Model notify the possible destinations, then I set the chosen one in the update method
                    model.findPossibleDestinations();
                    //I verify if the currPlayer won
                    win = model.hasWon();
                    break;
                case "BUILD":
                    model.findPossibleDestinations();
                    break;
                case "BOTH":
                    view.chooseNextStep();
                    break;
                case "END":
                    model.setNextPlayer();
                    currPlayer = model.getCurrPlayer();
                    break;
            }
        }
    }


    /**
     * the method game has to manage turns, match states and movement steps
     * cycling once for each setup phases and using a cycling iterator for the game one
     *
     */

    public void game() {
        String winner;
        while (currState != Model.State.ENDGAME) {
            switch (currState) {
                case SETUP_NUMOFPLAYERS:
                    view.askNumberOfPlayers();
                    break;

                case SETUP_PLAYERS:
                    setupPlayers();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;

                case SETUP_CARDS:
                    setupCards();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;

                case SETUP_BUILDERS:
                    setupBuilders();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;

                case GAME:
                    gameMoves();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;
            }
        }
        if (model.hasWon())
            winner = currPlayer.getNickname();
        else
            winner = model.getPlayers().get(0).getNickname();
        view.showWhoWon(winner);
    }


    @Override
    public void onBuilderBuild(Cell src, Cell dst, boolean buildDome) {
        model.effectiveBuild(src, dst, buildDome);
    }

    @Override
    public void onBuilderMove(Cell src, Cell dst) {
        model.effectiveMove(src, dst);
    }

    @Override
    public void onNicknameAndDateInsertion(String nickname, String birthday) {
        model.addPlayer(nickname, birthday);
    }

    /**
     * This update of Controller is called by a specific View's notify when the user insert a number
     * @param num is the number entered bu the user
     */
    @Override
    public void onNumberInsertion(int num) {
        if (model.setNumberOfPlayers(num)) {
            numPlayers = model.getNumPlayers();
            model.setNextState();
            currState = model.getCurrState();
        }
    }

    @Override
    public void onGodCardChoice(String godCardName) {
        model.assignCard(godCardName);
    }

    @Override
    public void onColorChoice(String chosenColor) {
        model.assignColor(chosenColor);
    }

    @Override
    public void onStepChoice(String chosenStep) {
        model.setStepChoice(chosenStep);
    }
}
