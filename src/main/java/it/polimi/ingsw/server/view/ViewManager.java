package it.polimi.ingsw.server.view;


import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.parser.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author thomas
 * Container for Virtual Views. Supposed to multiplex notifies coming from ModelObservable.
 */
public class ViewManager implements BuilderPossibleBuildObserver, BuilderPossibleMoveObserver, BuildersPlacedObserver,
                                    EndGameObserver, ErrorsObserver, PlayerLoseObserver, StateObserver,
        PlayerTurnObserver, ColorAssignmentObserver{

    List<VirtualView> views;

    public ViewManager() {
        views = new ArrayList<>();
    }

    public void add(VirtualView view) {
        views.add(view);
    }

    public void remove(String nickname) {
        views.removeIf(view -> view.getNickname().equals(nickname));
    }


    public void askNumberOfPlayers() {
        for (VirtualView view : views)
            view.send(Messages.askNumberOfPlayers());
    }

    public void askNickAndDate() {
        for(VirtualView view : views)
            view.send(Messages.askNickAndDate());

    }

    public void askColor(String player) {
        for(VirtualView view : views)
            if(view.getNickname().equals(player))
                view.send(Messages.askColor());
    }

    public void askGod(String player) {
        for(VirtualView view : views)
            if(view.getNickname().equals(player))
                view.send(Messages.askGod());
    }

    public void askBuilders(String player){
        for(VirtualView view : views)
            if(view.getNickname().equals(player))
                view.send(Messages.askBuilders());
    }

    //Observer Methods are multiplexed to the right VirtualViews

    @Override
    public void updatePossibleBuildDst(String nickname, Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.possibleBuildDestinations(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome));
    }

    @Override
    public void updatePossibleMoveDst(String nickname, Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.possibleMoveDestinations(possibleDstBuilder1, possibleDstBuilder2));
    }

    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2) {
        for(VirtualView view : views)
            view.send(Messages.buildersPlacement(nickname, positionBuilder1, positionBuilder2));
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        for(VirtualView view : views)
            view.send(Messages.endGame(winnerNickname));
    }

    @Override
    public void onWrongInsertionUpdate(String nickname, String error) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.errorMessage(error));
    }

    @Override
    public void onWrongNumberInsertion() {
        //TODO view.send(Messages.errorNumber());
    }

    @Override
    public void onWrongPlayerInsertion(String nickname) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.errorAddPlayer(nickname));
    }

    @Override
    public void onLossUpdate(String nickname) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.lostGame());
    }

    @Override
    public void onStateUpdate(Model.State currState) {
        //TODO da fare?
    }

    @Override
    public void onPlayerTurn(String nickname) {
        for (VirtualView view : views){
            if (view.getNickname().equals(nickname))
                view.send(Messages.turnUpdate(nickname));
        }
    }

    @Override
    public void onColorAssigned(String nickname) {
        for (VirtualView view : views){
            if (view.getNickname().equals(nickname))
                view.send(Messages.colorUpdate(nickname));
        }
    }
}
