package it.polimi.ingsw.interfaces;

import it.polimi.ingsw.interfaces.view.*;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.Set;

/**
 * Contains a list of observers and each of them is updated calling the notify method
 */
public abstract class ModelObservable {
    private StateObserver stateObserver;
    private BuilderPossibleMoveObserver possibleMoveObserver;
    private BuilderPossibleBuildObserver possibleBuildObserver;
    private PlayerLoseObserver playerLoseObserver;
    private ErrorsObserver errorsObserver;
    private EndGameObserver endGameObserver;
    private BuildersPlacedObserver buildersPlacedObserver;
    private BuilderMovementObserver builderMovementObserver;
    private BuilderBuiltObserver builderBuiltObserver;
    private PlayerTurnObserver playerTurnObserver;
    private ColorAssignmentObserver colorAssignmentObserver;
    private GodChoiceObserver godChoiceObserver;
    private PlayerAddedObserver playerAddedObserver;
    private ChosenStepObserver chosenStepObserver;
    private StartPlayerSetObserver startPlayerSetObserver;

    public void setStartPlayerSetObserver(StartPlayerSetObserver o) {
        startPlayerSetObserver = o;
    }

    public void setChosenStepObserver(ChosenStepObserver newChosenStepObserver){
        chosenStepObserver = newChosenStepObserver;
    }

    public void setPlayerAddedObserver(PlayerAddedObserver newPlayerAddedObserver) {
        playerAddedObserver = newPlayerAddedObserver;
    }

    public void setGodChoiceObserver(GodChoiceObserver newGodChoiceObserver){
        godChoiceObserver = newGodChoiceObserver;
    }

    public void setColorAssignmentObserver(ColorAssignmentObserver newColorAssignmentObserver) {
        colorAssignmentObserver = newColorAssignmentObserver;
    }

    public void setStateObserver(StateObserver newStateObserver) {
        stateObserver = newStateObserver;
    }

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

    public void setBuildersPlacedObserver(BuildersPlacedObserver newBuildersPlacedObservers) {
        buildersPlacedObserver = newBuildersPlacedObservers;}

    public void setBuilderBuiltObserver(BuilderBuiltObserver newBuilderBuiltObserver){
        builderBuiltObserver = newBuilderBuiltObserver;
    }

    public void setBuilderMovementObserver(BuilderMovementObserver newBuilderMovementObserver){
        builderMovementObserver = newBuilderMovementObserver;
    }

    public void setPlayerTurnObserver(PlayerTurnObserver newPlayerTurnObserver){
        playerTurnObserver = newPlayerTurnObserver;
    }


    public void notifyState (Model.State State) {
        if (stateObserver != null)
            stateObserver.onStateUpdate(State);
        else System.out.println("state observer is not set");
    }

    public void notifyPossibleMoves (Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        if(possibleMoveObserver != null)
            possibleMoveObserver.updatePossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2);
        else
            System.out.println("possibleMoveObserver is not set");
    }

    public void notifyPossibleBuilds (Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                      Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
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

    public void notifyWrongNumber (){
        if (errorsObserver != null)
            errorsObserver.onWrongNumberInsertion();
        else
            System.out.println("error observer is not set");
    }

    public void notifyEndGame (String winnerNick) {
        if (endGameObserver != null)
            endGameObserver.onEndGameUpdate(winnerNick);
        else
            System.out.println("endGame observer is not set");
    }

    public void notifyBuildersPlacement (String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        if (buildersPlacedObserver != null)
            buildersPlacedObserver.onBuildersPlacedUpdate(nickname, positionBuilder1, positionBuilder2, result);
        else
            System.out.println("buildersPlacement observer is not set");
    }

    public void notifyBuilderMovement (String nickname, Coordinates src, Coordinates dst, boolean result){
        if (builderMovementObserver != null)
            builderMovementObserver.onBuilderMovement(nickname, src, dst, result);
        else
            System.out.println("builder movement observer is not set");
    }

    public void notifyBuilderPushed(String nickname, Coordinates src, Coordinates dst) {
        if(builderMovementObserver != null)
            builderMovementObserver.onBuilderPushed(nickname, src, dst);
        else
            System.out.println("builder movement observer is not set");
    }

    public void notifyBuilderBuild (String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result){
        if (builderBuiltObserver != null)
            builderBuiltObserver.onBuilderBuild(nickname, src, dst, dome, result);
        else
            System.out.println("builder build observer is not set");
    }

    public void notifyPlayerTurn (String nickname){
        if (playerTurnObserver != null){
            playerTurnObserver.onPlayerTurn(nickname);
        }
        else
            System.out.println("player turn observer is not set");
    }

    public void notifyColorAssigned(String nickname, String color, boolean result){
        if (colorAssignmentObserver != null){
            colorAssignmentObserver.onColorAssigned(nickname, color, result);
        } else
            System.out.println("color assigned observer is not set");
    }

    public void notifyGodChoice(String nickname, String godCard, boolean result){
        if (godChoiceObserver != null){
            godChoiceObserver.onGodCardAssigned(nickname, godCard, result);
        } else
            System.out.println("color assigned observer is not set");
    }

    public void notifyMatchGodCards(Set<String> matchGodCards, boolean result) {
        if(godChoiceObserver != null) {
            godChoiceObserver.onMatchGodCardsAssigned(matchGodCards, result);
        } else
            System.out.println("color assigned observer is not set");

    }

    public void notifyPlayerAdded(String nickname, boolean result) {
        if(playerAddedObserver != null)
            playerAddedObserver.onPlayerAdded(nickname, result);
        else
            System.out.println("player added observer is not set");
    }

    public void notifyChosenStep(String nickname, String step, boolean result){
        if (chosenStepObserver != null)
            chosenStepObserver.onChosenStep(nickname, step, result);
        else
            System.out.println("step choice observer is not set");
    }

    public void notifyStartPlayerSet(String startPlayer, boolean result) {
        if(startPlayerSetObserver != null)
            startPlayerSetObserver.onStartPlayerSet(startPlayer, result);
        else
            System.out.println("start player set observer is not set");
    }
}
