package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.Set;

public abstract class ViewObservable {
    BuilderBuildObserver builderBuildObserver;
    BuilderMoveObserver builderMoveObserver;
    ColorChoiceObserver colorChoiceObserver;
    NewPlayerObserver newPlayerObserver;
    NumberOfPlayersObserver numberOfPlayersObserver;
    StepChoiceObserver stepChoiceObserver;
    BuilderSetupObserver builderSetupObserver;
    DisconnectionObserver disconnectionObserver;
    GodCardChoiceObserver godCardChoiceObserver;
    StartPlayerObserver startPlayerObserver;


    public void setObservers(Object o) {
        setBuilderBuildObserver((BuilderBuildObserver) o);
        setBuilderMoveObserver((BuilderMoveObserver) o);
        setColorChoiceObserver((ColorChoiceObserver) o);
        setNewPlayerObserver( (NewPlayerObserver) o);
        setNumberOfPlayersObserver((NumberOfPlayersObserver) o);
        setStepChoiceObserver((StepChoiceObserver) o);
        setBuilderSetupObserver((BuilderSetupObserver) o);
        setDisconnectionObserver((DisconnectionObserver) o);
        setGodCardChoiceObserver((GodCardChoiceObserver) o);
        setStartPlayerObserver((StartPlayerObserver) o);

    }


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

    public void setDisconnectionObserver(DisconnectionObserver o) {
        disconnectionObserver = o;
    }

    public void setGodCardChoiceObserver(GodCardChoiceObserver o) {
        godCardChoiceObserver = o;
    }

    protected void notifyBuild(String player, Coordinates src, Coordinates dst, boolean buildDome) {
        if(builderBuildObserver != null)
            builderBuildObserver.onBuilderBuild(player, src, dst, buildDome);
        else
            System.out.println("builder build observer is not set");
    }

    protected  void notifyMove(String player, Coordinates src, Coordinates dst) {
        if(builderMoveObserver != null)
            builderMoveObserver.onBuilderMove(player, src, dst);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifyColorChoice(String player, String color) {
        if(colorChoiceObserver != null)
            colorChoiceObserver.onColorChoice(player, color);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifyNewPlayer(String nickname, String birthday) {
        if(newPlayerObserver != null)
            newPlayerObserver.onNicknameAndDateInsertion(nickname, birthday);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifyNumberOfPlayers(int numberOfPlayers) {
        if(numberOfPlayersObserver != null)
            numberOfPlayersObserver.onNumberInsertion(numberOfPlayers);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifyStepChoice(String player, String step) {
        if(stepChoiceObserver != null)
            stepChoiceObserver.onStepChoice(player, step);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifySetupBuilders(String player, Coordinates pos1, Coordinates pos2) {
        if(builderSetupObserver != null)
            builderSetupObserver.onBuilderSetup(player, pos1, pos2);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifyDisconnection(String player) {
        if(disconnectionObserver != null)
            disconnectionObserver.onDisconnection(player);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifyGodCardChoice(String player, String godCard) {
        if(godCardChoiceObserver != null)
            godCardChoiceObserver.onGodCardChoice(player, godCard);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifyMatchGodCardsChoice(String nickname, Set<String> chosenGodCards) {
        if(godCardChoiceObserver != null)
            godCardChoiceObserver.onMatchGodCardsChoice(nickname, chosenGodCards);
        else
            System.out.println("builder build observer is not set");
    }

    protected void notifySetStartPlayer(String nickname, String startPlayer) {
        if(startPlayerObserver != null)
            startPlayerObserver.onSetStartPlayer(nickname, startPlayer);
        else
            System.out.println("builder build observer is not set");
    }

}
