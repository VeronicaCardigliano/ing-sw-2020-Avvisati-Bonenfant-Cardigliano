package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.*;

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
    private EndGameObserver endGameObserver;
    private BuildersPlacementObserver buildersPlacementObserver;



    public void setErrorsObserver (ErrorsObserver newErrorsObserver) {
        errorsObserver = newErrorsObserver;}

    public void setEndGameObserver (EndGameObserver newEndGameObserver) {
        endGameObserver = newEndGameObserver;}

    public void setPossibleMoveObserver (BuilderPossibleMoveObserver newPossibleMoveObserver) {
        possibleMoveObserver = newPossibleMoveObserver;}

    public void setPossibleBuildObserver (BuilderPossibleBuildObserver newPossibleBuildObserver) {
        possibleBuildObserver = newPossibleBuildObserver;}

    public void setPlayerLoseObserver (PlayerLoseObserver newPlayerLoseObserver) {
        playerLoseObserver = newPlayerLoseObserver;}

    public void setBuildersPlacementObserver (BuildersPlacementObserver newBuildersPlacementObservers) {
        buildersPlacementObserver = newBuildersPlacementObservers;}

    /*
    public void notifyState (Model.State State) {
            stateObserver.onStateUpdate(State);
    } */

    public void notifyPossibleMoves (String currPlayer, Set possibleDstBuilder1, Set possibleDstBuilder2) {
        if(possibleMoveObserver != null)
            possibleMoveObserver.updatePossibleMoveDst(currPlayer, possibleDstBuilder1, possibleDstBuilder2);
        else
            System.out.println("possibleMoveObserver is not set");
    }

    public void notifyPossibleBuilds (String currPlayer, Set possibleDstBuilder1, Set possibleDstBuilder2,
                                      Set possibleDstBuilder1forDome, Set possibleDstBuilder2forDome) {
        if(possibleBuildObserver != null)
            possibleBuildObserver.updatePossibleBuildDst(currPlayer, possibleDstBuilder1, possibleDstBuilder2,
                possibleDstBuilder1forDome, possibleDstBuilder2forDome);
        else
            System.out.println("possibleBuildObserver is not set");
    }

    public void notifyLoss(String currPlayer) {
        if(playerLoseObserver != null)
            playerLoseObserver.onLossUpdate(currPlayer, currPlayer);
        else
            System.out.println("playerLoseObserver is not set");
    }

    public void notifyWrongInsertion (String currPlayer, String error){
        if (errorsObserver != null)
            errorsObserver.onWrongInsertionUpdate(currPlayer, error);
        else
            System.out.println("error observer is not set");
    }

    public void notifyEndGame (String winnerNick) {
        if (endGameObserver != null)
            endGameObserver.onEndGameUpdate(winnerNick);
        else
            System.out.println("endGame observer is not set");
    }

    public void notifyBuildersPlacement (Coordinates positionBuilder1, Coordinates positionBuilder2) {
        if (buildersPlacementObserver != null)
            buildersPlacementObserver.onBuildersPlacementUpdate(positionBuilder1, positionBuilder2);
        else
            System.out.println("buildersPlacement observer is not set");
    }
}
