package it.polimi.ingsw.view;

import it.polimi.ingsw.Observable;
import it.polimi.ingsw.model.Model;

import java.util.List;

/**
 * view class notifies Controller (Observable) and is notified by Model (observer)
 */
public class View extends Observable implements StateObserver, BuilderPossibleMoveObserver, BuilderPossibleBuildObserver {



    @Override
    public void onStateUpdate(Model.State currState) {

    }

    @Override
    public void updatePossibleBuildDst(List possibleDstBuilder1, List possibleDstBuilder2, List possibleDstBuilder1forDome, List possibleDstBuilder2forDome) {

    }

    @Override
    public void updatePossibleMoveDst(List possibleDstBuilder1, List possibleDstBuilder2) {

    }
}
