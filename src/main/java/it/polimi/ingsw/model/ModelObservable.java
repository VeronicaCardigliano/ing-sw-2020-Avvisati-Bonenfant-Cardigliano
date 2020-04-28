package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.view.*;

import java.util.List;

/**
 * has a list of observers and each of them is updated calling the notify method
 */
public class ModelObservable {
    private StateObserver stateObserver;
    private BuilderPossibleMoveObserver possibleMoveObserver;
    private BuilderPossibleBuildObserver possibleBuildObserver;
    private PlayerLoseObserver playerLoseObserver;
    private ErrorsObserver errorsObserver;

    //private List<Observer<T>> observers = new ArrayList<>();


    public void setStateObserver (StateObserver newObserver) {
        stateObserver = newObserver;
    }

    public void notifyState (Model.State State) {
            stateObserver.onStateUpdate(State);
    }

    public void notifyPossibleMoves (List possibleDstBuilder1, List possibleDstBuilder2) {
        possibleMoveObserver.updatePossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2);
    }

    public void notifyPossibleBuilds (List possibleDstBuilder1, List possibleDstBuilder2,
                                      List possibleDstBuilder1forDome, List possibleDstBuilder2forDome) {
        possibleBuildObserver.updatePossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2,
                possibleDstBuilder1forDome, possibleDstBuilder2forDome);
    }

    public void notifyLoss() {
        playerLoseObserver.onLossUpdate();
    }

    public void notifyWrongInsertion (String error){
        errorsObserver.onWrongInsertionUpdate(error);
    }

}
