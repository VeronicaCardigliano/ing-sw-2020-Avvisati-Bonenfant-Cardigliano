package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Player;


public class ViewObservable {
    private NumberOfPlayersObserver numberOfPlayersObserver;
    private BuilderBuildObserver buildObserver;
    private BuilderMoveObserver moveObserver;
    private NewPlayerObserver newPlayerObserver;
    private GodCardChoiceObserver godCardChoiceObserver;
    private ColorChoiceObserver colorChoiceObserver;
    private StepChoiceObserver stepChoiceObserver;

    public void notifyNumberOfPlayers (int num) {
        numberOfPlayersObserver.onNumberInsertion(num);
    }

    public void notifyNewPlayer (String nickname, String birthday) {
        newPlayerObserver.onNicknameAndDateInsertion(nickname, birthday);
    }

    public void notifyGodCardChoice (String godCardName) {
        godCardChoiceObserver.onGodCardChoice(godCardName);
    }

    public void notifyColorChoice (String chosenColor) {
        colorChoiceObserver.onColorChoice(chosenColor);
    }

    public void notifyMoveChoice (Cell src, Cell dst) {
        moveObserver.onBuilderMove(src, dst);
    }

    public void notifyBuildChoice (Cell src, Cell dst, boolean buildDome) {
        buildObserver.onBuilderBuild(src, dst, buildDome);
    }

    public void notifyStepChoice (String chosenStep) {
        stepChoiceObserver.onStepChoice(chosenStep);
    }

}
