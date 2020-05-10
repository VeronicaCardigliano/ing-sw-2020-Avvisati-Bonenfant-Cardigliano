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
    PlayerDeletionObserver playerDeletionObserver;
    DisconnectionObserver disconnectionObserver;
    GodCardChoiceObserver godCardChoiceObserver;

    private static final boolean DEBUG = true;

    protected void notifyBuild(String player, Coordinates src, Coordinates dst, boolean buildDome) {
        if(!DEBUG)
            builderBuildObserver.onBuilderBuild(player, src, dst, buildDome);
    }

    protected  void notifyMove(String player, Coordinates src, Coordinates dst) {
        if(!DEBUG)
            builderMoveObserver.onBuilderMove(player, src, dst);
    }

    protected void notifyColorChoice(String player, String color) {
        if(!DEBUG)
            colorChoiceObserver.onColorChoice(player, color);
    }

    protected void notifyNewPlayer(String nickname, String birthday) {
        if(!DEBUG)
            newPlayerObserver.onNicknameAndDateInsertion(nickname, birthday);
    }

    protected void notifyNumberOfPlayers(int numberOfPlayers) {
        if(!DEBUG)
            numberOfPlayersObserver.onNumberInsertion(numberOfPlayers);
    }

    protected void notifyStepChoice(String player, String step) {
        if(!DEBUG)
            stepChoiceObserver.onStepChoice(player, step);
    }

    protected void notifySetupBuilders(String player, Coordinates pos1, Coordinates pos2) {
        if(!DEBUG)
            builderSetupObserver.onBuilderSetup(player, pos1, pos2);
    }

    protected void notifyPlayerDeletion(String player) {
        if(!DEBUG)
            playerDeletionObserver.onPlayerDeleted(player);
    }

    protected void notifyDisconnection(String player) {
        if(!DEBUG)
            disconnectionObserver.onDisconnection(player);
    }

    protected void notifyGodCardChoice(String player, String godCard) {
        if(!DEBUG)
            godCardChoiceObserver.onGodCardChoice(player, godCard);
    }

}
