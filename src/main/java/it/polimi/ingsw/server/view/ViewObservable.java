package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.*;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

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

    /*public ViewObservable(Object c) {

        builderBuildObserver = (BuilderBuildObserver) c;
        builderMoveObserver = (BuilderMoveObserver) c;
        colorChoiceObserver = (ColorChoiceObserver) c;
        newPlayerObserver =(NewPlayerObserver) c;
        numberOfPlayersObserver = (NumberOfPlayersObserver) c;
        stepChoiceObserver = (StepChoiceObserver) c;
        builderSetupObserver = (BuilderSetupObserver) c;
        disconnectionObserver = (DisconnectionObserver) c;
        godCardChoiceObserver = (GodCardChoiceObserver) c;
    }*/

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
    }

    private static final boolean DEBUG = false;

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
    }

    protected  void notifyMove(String player, Coordinates src, Coordinates dst) {
        if(builderMoveObserver != null)
            builderMoveObserver.onBuilderMove(player, src, dst);
    }

    protected void notifyColorChoice(String player, String color) {
        if(colorChoiceObserver != null)
            colorChoiceObserver.onColorChoice(player, color);
    }

    protected void notifyNewPlayer(String nickname, String birthday) {
        if(newPlayerObserver != null)
            newPlayerObserver.onNicknameAndDateInsertion(nickname, birthday);
    }

    protected void notifyNumberOfPlayers(int numberOfPlayers) {
        if(numberOfPlayersObserver != null)
            numberOfPlayersObserver.onNumberInsertion(numberOfPlayers);
    }

    protected void notifyStepChoice(String player, String step) {
        if(stepChoiceObserver != null)
            stepChoiceObserver.onStepChoice(player, step);
    }

    protected void notifySetupBuilders(String player, Coordinates pos1, Coordinates pos2) {
        if(builderSetupObserver != null)
            builderSetupObserver.onBuilderSetup(player, pos1, pos2);
    }

    protected void notifyDisconnection(String player) {
        if(disconnectionObserver != null)
            disconnectionObserver.onDisconnection(player);
    }

    protected void notifyGodCardChoice(String player, String godCard) {
        if(godCardChoiceObserver != null)
            godCardChoiceObserver.onGodCardChoice(player, godCard);
    }


}
