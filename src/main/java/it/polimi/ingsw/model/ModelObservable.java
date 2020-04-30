package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.view.*;

import java.util.List;
import java.util.Set;

/**
 * has a list of observers and each of them is updated calling the notify method
 */
public class ModelObservable {
    //private StateObserver stateObserver;
    private BuilderPossibleMoveObserver possibleMoveObserver;
    private BuilderPossibleBuildObserver possibleBuildObserver;
    private PlayerLoseObserver playerLoseObserver;
    private ErrorsObserver errorsObserver;

    //private List<Observer<T>> observers = new ArrayList<>();


    public void setErrorsObserver (ErrorsObserver newErrorsObserver) {
        errorsObserver = newErrorsObserver;}

    /*
    public void notifyState (Model.State State) {
            stateObserver.onStateUpdate(State);
    } */

    public void notifyPossibleMoves (Set possibleDstBuilder1, Set possibleDstBuilder2) {
        if(possibleMoveObserver != null)
            possibleMoveObserver.updatePossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2);
        else
            System.out.println("possibleMoveObserver is not set");
    }

    public void notifyPossibleBuilds (Set possibleDstBuilder1, Set possibleDstBuilder2,
                                      Set possibleDstBuilder1forDome, Set possibleDstBuilder2forDome) {
        if(possibleBuildObserver != null)
            possibleBuildObserver.updatePossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2,
                possibleDstBuilder1forDome, possibleDstBuilder2forDome);
        else
            System.out.println("possibleBuildObserver is not set");
    }

    public void notifyLoss(String currPlayer) {
        if(playerLoseObserver != null)
            playerLoseObserver.onLossUpdate(currPlayer);
        else
            System.out.println("playerLoseObserver is not set");
    }

    public void notifyWrongInsertion (String error){
        if (errorsObserver != null)
            errorsObserver.onWrongInsertionUpdate(error);
        else
            System.out.println("error observer is not set");
    }

}
