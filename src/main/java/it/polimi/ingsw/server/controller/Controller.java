package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.VirtualView;

//TODO: make sure of not passing editable objects

public class Controller implements BuilderBuildObserver, BuilderMoveObserver, NewPlayerObserver, NumberOfPlayersObserver,
            GodCardChoiceObserver, ColorChoiceObserver, StepChoiceObserver {

    private Model model;
    private VirtualView virtualView;
    private int numPlayers;
    private Player currPlayer;

    private Model.State currState;

    public Controller (Model model, VirtualView virtualView) {

        this.model = model;
        this.virtualView = virtualView;

    }

    private void setupCards () {
        int i = 0;
        while (i < numPlayers) {
            //prints the GodNames and their description only of still available cards
            //virtualView.chooseGodCard(model.getGodDescriptions(), model.getChosenCards());

            if (!(currPlayer.getGodCard() == null)) {
                i++;
                model.setNextPlayer();
                currPlayer = model.getCurrPlayer();
            }
        }
    }

    private void setupBuilders () {
        int i = 0;
        while (i < numPlayers) {

            //virtualView.chooseBuilderColor(model.getChosenColors());

            if(!(currPlayer.getBuilders() == null)) {
                i++;
                model.setNextPlayer();
                currPlayer = model.getCurrPlayer();
            }
        }
    }

    private void gameMoves () {
        while (!model.endGame()) {
            //the Step is updated after every effective move or build from the methods themselves (move and build)
            switch (model.getCurrStep(currPlayer)) {
                case "MOVE":
                case "BUILD":
                    //I send through a Model notify the possible destinations, then I set the chosen one in the update method
                    model.findPossibleDestinations();
                    break;
                case "BOTH":
                    //virtualView.chooseNextStep();
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
        currState = model.getCurrState();
        while (currState != Model.State.ENDGAME) {
            switch (currState) {
                case SETUP_NUMOFPLAYERS:
                    //virtualView.askNumberOfPlayers();
                    break;

                case SETUP_PLAYERS:
                    while (model.getPlayers().isEmpty() || model.getPlayers().size() < numPlayers)
                       // virtualView.askForNewPlayer();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;

                case SETUP_CARDS:
                    //returns to the first player
                    model.setNextPlayer();
                    currPlayer = model.getCurrPlayer();

                    setupCards();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;

                case SETUP_BUILDERS:
                    //returns to the first player
                    model.setNextPlayer();
                    currPlayer = model.getCurrPlayer();

                    setupBuilders();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;

                case GAME:
                    //returns to the first player
                    model.setNextPlayer();
                    currPlayer = model.getCurrPlayer();

                    gameMoves();
                    model.setNextState();
                    currState = model.getCurrState();
                    break;
            }
        }
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
