package it.polimi.ingsw.server.view;


import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ViewManager implements BuilderPossibleBuildObserver, BuilderPossibleMoveObserver, BuildersPlacementObserver,
                                    EndGameObserver, ErrorsObserver, PlayerLoseObserver, StateObserver{

    List<VirtualView> views;

    public ViewManager() {
        views = new ArrayList<>();
    }

    public void add(VirtualView view) {
        views.add(view);
    }


    @Override
    public void updatePossibleBuildDst(String nickname, Set possibleDstBuilder1, Set possibleDstBuilder2, Set possibleDstBuilder1forDome, Set possibleDstBuilder2forDome) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.updatePossibleBuildDst(nickname, possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome);
    }

    @Override
    public void updatePossibleMoveDst(String nickname, Set possibleDstBuilder1, Set possibleDstBuilder2) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.updatePossibleMoveDst(nickname, possibleDstBuilder1, possibleDstBuilder2);
    }

    @Override
    public void onBuildersPlacementUpdate(Coordinates positionBuilder1, Coordinates positionBuilder2) {
        for(VirtualView view : views)
            view.onBuildersPlacementUpdate(positionBuilder1, positionBuilder2);
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        for(VirtualView view : views)
            view.onEndGameUpdate(winnerNickname);
    }

    @Override
    public void onWrongInsertionUpdate(String nickname, String error) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.onWrongInsertionUpdate(nickname, error);
    }

    @Override
    public void onLossUpdate(String nickname, String currPlayer) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname));
    }

    @Override
    public void onStateUpdate(Model.State currState) {
        //TODO da fare?
    }
}
