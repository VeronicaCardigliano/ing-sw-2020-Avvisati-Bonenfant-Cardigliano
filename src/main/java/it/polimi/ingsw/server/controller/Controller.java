package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;

//TODO: make sure of not passing editable objects

public class Controller implements BuilderBuildObserver, BuilderMoveObserver, NewPlayerObserver, NumberOfPlayersObserver,
            GodCardChoiceObserver, ColorChoiceObserver, StepChoiceObserver {

    private Model model;
    private ViewManager viewManager;

    public Controller (Model model, ViewManager viewManager) {

        this.model = model;
        this.viewManager = viewManager;

    }

    /**
     * This update of Controller is called by a specific View's notify when the user insert a number
     * @param num is the number entered bu the user
     */
    @Override
    public void onNumberInsertion(int num) {

        if (model.getCurrState() == Model.State.SETUP_NUMOFPLAYERS && model.getNumberOfPlayers() != 2 ||
        model.getNumPlayers() != 3) {

            if (model.setNumberOfPlayers(num)) {
                //TODO viewManager.askNick&Date() //broadcast message
                model.setNextState();
            }

        }
    }


    @Override
    public void onNicknameAndDateInsertion(String nickname, String birthday) {

        if (model.getCurrState() == Model.State.SETUP_PLAYERS) {

            if (model.getPlayers().size() < model.getNumPlayers()){
            model.addPlayer(nickname, birthday);
            }

            if (model.getPlayers().size() == model.getNumPlayers()) {
                model.setNextState();
                //setNextPlayer used to initialize first player :)
                model.setNextPlayer();
                //TODO viewManager.askColor(model.currPlayer().getNickname());
            }

        }
    }


    @Override
    public void onColorChoice(String player, String color){

        if (model.getCurrState() == Model.State.SETUP_COLOR && model.getCurrPlayer().getNickname().equals(player)){
            model.assignColor(color);
            model.setNextPlayer();
            if (model.getCurrPlayer().equals(model.getPlayers().get(0)))
                model.setNextState();
            else;//TODO viewManager.askGod(model.getCurrPlayer().getNickname());

        }
    }

    @Override
    public void onGodCardChoice(String player, String godCardName) {
        if (model.getCurrState() == Model.State.SETUP_CARDS && model.getCurrPlayer().getNickname().equals(player) &&
                model.getCurrPlayer().getGodCard() == null) {

                if (model.assignCard(godCardName)) {
                    //Initialize the turn
                    model.getCurrPlayer().getGodCard().startTurn();
                    model.setNextPlayer();
                    if (model.getCurrPlayer().equals(model.getPlayers().get(0)))
                        model.setNextState();
                    else; //TODO viewManager.askGod(model.getCurrPlayer().getNickname());
                }
            }
        }

//-------------

    @Override
    public void onBuilderBuild(String player, Coordinates src, Coordinates dst, boolean buildDome) {
        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().getNickname().equals(player))
            if(model.effectiveBuild(src,dst,buildDome)){
                if (model.getCurrPlayer().getGodCard().getCurrState().equals("END")){
                    model.setNextPlayer();
                    model.getCurrPlayer().startTurn();
                }
            }
    }


    @Override
    public void onBuilderMove(String player, Coordinates src, Coordinates dst) {
        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().getNickname().equals(player))
            if(model.effectiveMove(src, dst)){
                if (model.getCurrPlayer().getGodCard().getCurrState().equals("END")){
                    model.setNextPlayer();
                    model.getCurrPlayer().startTurn();
            }
        }
    }


//------------

    @Override
    public void onStepChoice(String player, String chosenStep) {
        if (model.getCurrPlayer().getNickname().equals(player) && model.getCurrPlayer().getGodCard().getCurrState().equals("BOTH"))
        model.setStepChoice(chosenStep);
    }
}