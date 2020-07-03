package it.polimi.ingsw.interfaces;

import it.polimi.ingsw.interfaces.controller.*;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.Set;

/**
 * Base Class for View part of MVC. It is implemented by View and VirtualView.
 * Contains different observers for every event generated by user that Controller should handle
 */
public abstract class ViewObservable {
    BuilderBuildObserver builderBuildObserver;
    BuilderMoveObserver builderMoveObserver;
    ColorChoiceObserver colorChoiceObserver;
    NewPlayerObserver newPlayerObserver;
    NumberOfPlayersObserver numberOfPlayersObserver;
    StepChoiceObserver stepChoiceObserver;
    BuilderSetupObserver builderSetupObserver;
    GodCardChoiceObserver godCardChoiceObserver;
    StartPlayerObserver startPlayerObserver;

    public void setStartPlayerObserver(StartPlayerObserver o) { startPlayerObserver = o;}

    public void setBuilderBuildObserver(BuilderBuildObserver o) {
        builderBuildObserver = o;
    }

    public void setBuilderMoveObserver(BuilderMoveObserver o) {
        builderMoveObserver = o;
    }

    public void setColorChoiceObserver(ColorChoiceObserver o) {
        colorChoiceObserver = o;
    }

    public void setNewPlayerObserver(NewPlayerObserver o) {
        newPlayerObserver = o;
    }

    public void setNumberOfPlayersObserver(NumberOfPlayersObserver o) {
        numberOfPlayersObserver = o;
    }

    public void setStepChoiceObserver(StepChoiceObserver o) {
        stepChoiceObserver = o;
    }

    public void setBuilderSetupObserver(BuilderSetupObserver o) {
        builderSetupObserver = o;
    }

    public void setGodCardChoiceObserver(GodCardChoiceObserver o) {
        godCardChoiceObserver = o;
    }

    /**
     * notifies a BuilderBuildObserver about a build request. It calls its onBuilderBuild handler.
     * @see BuilderBuildObserver
     */
    protected void notifyBuild(String player, Coordinates src, Coordinates dst, boolean buildDome) {
        if(builderBuildObserver != null)
            builderBuildObserver.onBuilderBuild(player, src, dst, buildDome);
        else
            System.out.println("builder build observer is not set");
    }

    /**
     * notifies a BuildeMoveObserver about a move request. It calls it onBuilderMove handler.
     * @see BuilderMoveObserver
     */
    protected  void notifyMove(String player, Coordinates src, Coordinates dst) {
        if(builderMoveObserver != null)
            builderMoveObserver.onBuilderMove(player, src, dst);
        else
            System.out.println("builder move observer is not set");
    }

    /**
     * notifies a ColorChoiceObserver about a color choice request. It calls its onColorChoice handler.
     * @see ColorChoiceObserver
     */
    protected void notifyColorChoice(String player, String color) {
        if(colorChoiceObserver != null)
            colorChoiceObserver.onColorChoice(player, color);
        else
            System.out.println("color choice observer is not set");
    }

    /**
     * notifies a NewPlayerObserver about a new player registration. It calls its onNicknameAndDateInsertion handler
     * @see NewPlayerObserver
     */
    protected void notifyNewPlayer(String nickname, String birthday) {
        if(newPlayerObserver != null)
            newPlayerObserver.onNicknameAndDateInsertion(nickname, birthday);
        else
            System.out.println("new player observer is not set");
    }

    /**
     * notifies a NumberOfPlayersObserver about a number of players setup. It calls its onNumberInsertion handler.
     * @see NumberOfPlayersObserver
     */
    protected void notifyNumberOfPlayers(int numberOfPlayers) {
        if(numberOfPlayersObserver != null)
            numberOfPlayersObserver.onNumberInsertion(numberOfPlayers);
        else
            System.out.println("number of players observer is not set");
    }

    /**
     * notifies a StepChoiceObserver about a step choice request. It calls its onStepChoice handler.
     * @see StepChoiceObserver
     */
    protected void notifyStepChoice(String player, String step) {
        if(stepChoiceObserver != null)
            stepChoiceObserver.onStepChoice(player, step);
        else
            System.out.println("step choice observer is not set");
    }

    /**
     * notifies a BuilderSetupObserver about a builders setup request. It calls its onBuilderSetup handler.
     * @see BuilderSetupObserver
     */
    protected void notifySetupBuilders(String player, Coordinates pos1, Coordinates pos2) {
        if(builderSetupObserver != null)
            builderSetupObserver.onBuilderSetup(player, pos1, pos2);
        else
            System.out.println("builder setup observer is not set");
    }

    /**
     * notifies a GodCardChoiceObserver about a god card choice request. It calls its onGodCardChoice handler.
     * @see GodCardChoiceObserver
     */
    protected void notifyGodCardChoice(String player, String godCard) {
        if(godCardChoiceObserver != null)
            godCardChoiceObserver.onGodCardChoice(player, godCard);
        else
            System.out.println("god card choice observer is not set");
    }

    /**
     * notifies a GodCardChoice about a match god Cards choice request. It calls its onMatchGodCardsChoice handler.
     * @see GodCardChoiceObserver
     */
    protected void notifyMatchGodCardsChoice(String nickname, Set<String> chosenGodCards) {
        if(godCardChoiceObserver != null)
            godCardChoiceObserver.onMatchGodCardsChoice(nickname, chosenGodCards);
        else
            System.out.println("match god cards choice observer is not set");
    }

    /**
     * notifies a StartPlayerObserver about a start player choice request. It calls its onSetStartPlayer handler.
     * @see StartPlayerObserver
     */
    protected void notifySetStartPlayer(String nickname, String startPlayer) {
        if(startPlayerObserver != null)
            startPlayerObserver.onSetStartPlayer(nickname, startPlayer);
        else
            System.out.println("start player observer is not set");
    }

}